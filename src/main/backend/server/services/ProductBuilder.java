package server.services;
import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.config.AliasConfig;
import server.domain.OriginalProduct;
import server.domain.Product;
import server.domain.UniqueBrand;
import server.repos.BrandsRepo;
import server.repos.OriginalRepo;
import server.repos.ProductRepo;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class ProductBuilder {
    private final OriginalRepo originalRepo;
    private final ProductRepo productRepo;
    private final AliasConfig aliasConfig;
    private final BrandsRepo brandsRepo;

    public void updateProductsDB(MultipartFile excelFile) throws FileNotFoundException {
        log.info(excelFile.getOriginalFilename());
        parseSupplierFile(excelFile);
        matchProductDetails();
        resolveDuplicates();
        resolveAvailable();
        log.info("Update Complete! Products available: " + productRepo.findAll().size());
    }

    /*#0*/
    /*Обновление данных таблицы поставщиков*/
    private void parseSupplierFile(MultipartFile excelFile) {
        if (!Objects.requireNonNull(excelFile.getOriginalFilename()).isEmpty())
        {
            log.info("Парсинг: " + excelFile.getOriginalFilename());
            try
            {
                int countCreate = 0, countUpdate = 0;
                boolean supplierRBT = excelFile.getOriginalFilename().contains("СП2");

                /// openBook(excelFile)
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("D:\\IT\\Dev\\ExpertStoreDev\\Prices\\" + excelFile.getOriginalFilename())));
                XSSFSheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet)
                {
                    if (lineIsCorrect(row, supplierRBT)) {
                        String productID = resolveProductID(supplierRBT, row);

                        if (originalProductExists(productID)) {
                            updateOriginalProduct(row, productID, supplierRBT);
                            countUpdate++;
                        }
                        else {
                            createOriginalProduct(row, productID, supplierRBT);
                            countCreate++;
                        }
                    }
                }
                log.info("Всего строк: " + sheet.getLastRowNum());
                log.info("Создано: " + countCreate);
                log.info("Обновлено: " + countUpdate);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            catch (OLE2NotOfficeXmlFileException e) {
                log.warning("Формат Excel документа должен быть XLSX!");
            }
        }
    }

    private void matchProductDetails() throws FileNotFoundException {
        log.info("Маппинг группы и параметров...");
        int countJSON = 0, countDefault = 0;
        List<OriginalProduct> originalProducts = originalRepo.findByUpdateDate(LocalDate.now());
        LinkedHashMap<String, String> aliases = aliasConfig.aliasesMap();

        for (OriginalProduct originalProduct : originalProducts)
        {
            String productID = originalProduct.getProductID();
            Product product = productRepo.findProductByProductID(productID);
            if (product == null) {
                product = new Product();
                product.setProductID(productID);
            }
            /*matchGroup()*/
            String originalGroup = originalProduct.getOriginalType();
            mapping: for (Map.Entry<String,String> aliasEntry : aliases.entrySet())
            {
                for (String alias : aliasEntry.getKey().split(","))
                {
                    if (StringUtils.startsWithIgnoreCase(originalGroup, alias)) {
                        product = matchProductJSON(aliasEntry, product);
                        countJSON++;
                        break mapping;
                    }
                }
            }
            if (!product.getMappedJSON()) {
                product = defaultProductMatch(originalProduct, product);
                countDefault++;
            }
            product = resolveProductsDetails(originalProduct, product);
            productRepo.save(product);
        }
        log.info("JSON разметка: " + countJSON);
        log.info("Auto разметка: " + countDefault);
    }

    private Product resolveProductsDetails(OriginalProduct originalProduct, Product product) throws NumberFormatException {
        String singleTypeName = product.getSingleTypeName();
        String originalBrand  = originalProduct.getOriginalBrand();
        String originalName   = originalProduct.getOriginalName();
        boolean supplierRBT   = originalProduct.getSupplier().equals("RBT");

        Integer finalPrice    = resolveFinalPrice(originalProduct, product.getDefaultCoefficient());
        String modelName      = resolveModelName(originalProduct).toUpperCase().trim();
        String annotation     = resolveAnnotation(originalProduct, supplierRBT);
        String shortAnnotation = resolveShortAnnotation(originalProduct, supplierRBT);

        Integer bonus         = resolveBonus(finalPrice);
        String fullName       = resolveFullName(originalName, modelName, singleTypeName, originalBrand).trim();
        String groupBrandName = resolveBrandName(singleTypeName, originalBrand).trim();
        String searchName     = resolveSearchName(modelName, singleTypeName, originalBrand).trim();
        String shortModelName = resolveShortModel(modelName, singleTypeName, originalBrand).trim();

        product.setOriginalName(originalName);
        product.setFinalPrice(finalPrice);
        product.setBonus(bonus);
        product.setModelName(modelName);
        product.setFullName(fullName);
        product.setGroupBrandName(groupBrandName);
        product.setSearchName(searchName);
        product.setShortModelName(shortModelName);
        product.setAnnotation(annotation);
        product.setShortAnnotation(shortAnnotation);

        product.setSupplier(originalProduct.getSupplier());
        product.setPic(originalProduct.getOriginalPicLink());
        product.setBrand(originalBrand);
        product.setUpdateDate(LocalDate.now());
        return product;
    }

    private String resolveShortAnnotation(OriginalProduct originalProduct, boolean supplierRBT) {
        String originalAnnotation = originalProduct.getOriginalAnnotation();

        if (!originalAnnotation.isEmpty())
        {
            String splitter  = supplierRBT ? "; " : ", ";
            String[] stopList = {": нет", ": 0",  ": -", "количество шт в"};

            List<String> filters = new LinkedList<>(Arrays.asList(originalProduct.getOriginalAnnotation().split(splitter)));
            filters.removeIf(filter -> Arrays.stream(stopList).parallel().anyMatch(filter::contains));

            return filters.toString().replaceAll("\\[|\\]","");
        }
        return "Original Annotation is empty!";
    }

    private void createOriginalProduct(Row row, String productID, boolean supplierRBT) {
        try
        {
            OriginalProduct originalProduct = new OriginalProduct();
            originalProduct.setProductID(productID);

            String originalCategory     = supplierRBT ? row.getCell(1).toString() : row.getCell(0).toString();
            String originalGroup        = supplierRBT ? "" : row.getCell(1).toString();
            String originalType         = supplierRBT ? row.getCell(2).toString() : row.getCell(3).toString();
            String originalName         = supplierRBT ? row.getCell(3).getStringCellValue() : row.getCell(6).toString();
            String originalAnnotation   = supplierRBT ? row.getCell(5).toString() : "";
            String originalPrice        = supplierRBT ? row.getCell(7).toString().trim() : row.getCell(13).toString().trim();
            String originalAmount       = supplierRBT ? row.getCell(6).toString() : row.getCell(7).toString().concat(row.getCell(8).toString());
            String originalBrand        = row.getCell(4).toString();
            String supplier             = supplierRBT ? "RBT" : "RUSBT";

            String originalPicLink;
            if (supplierRBT) {
                originalPicLink = StringUtils.substringBetween(row.getCell(3).toString(), "\"", "\""); /*при попытке row.getCell(3).getHyperlink().getAddress() вылетает NullPointer*/
            }
            else if (!row.getCell(15).toString().isEmpty()) {
                originalPicLink = row.getCell(15).getHyperlink().getAddress();
            }
            else originalPicLink = "Ссылки нет!";

            originalProduct.setOriginalCategory(originalCategory);
            originalProduct.setOriginalGroup(originalGroup);
            originalProduct.setOriginalType(originalType);
            originalProduct.setOriginalName(originalName);
            originalProduct.setOriginalAnnotation(originalAnnotation);
            originalProduct.setOriginalPrice(originalPrice);
            originalProduct.setOriginalAmount(originalAmount);
            originalProduct.setOriginalBrand(originalBrand);
            originalProduct.setSupplier(supplier);
            originalProduct.setOriginalPicLink(originalPicLink);
            originalProduct.setUpdateDate(LocalDate.now());
            originalProduct.setIsAvailable(true);
            originalRepo.save(originalProduct);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateOriginalProduct(Row row, String productID, boolean supplierRBT) {
        OriginalProduct originalProduct = originalRepo.findByProductID(productID);

        String newOriginalPrice  = supplierRBT ? row.getCell(7).toString() : row.getCell(13).toString();
        String newOriginalAmount = supplierRBT ? row.getCell(6).toString() : row.getCell(7).toString().concat(row.getCell(8).toString());

        originalProduct.setOriginalPrice(newOriginalPrice);
        originalProduct.setOriginalAmount(newOriginalAmount);
        originalProduct.setUpdateDate(LocalDate.now());
        originalProduct.setIsAvailable(true);
        originalRepo.save(originalProduct);
    }

    private boolean originalProductExists(String productID) {
        return originalRepo.findByProductID(productID) != null;
    }

    private String resolveProductID(boolean supplierRBT, Row row) {
        return supplierRBT ? row.getCell(0).toString() : row.getCell(5).toString().replaceAll("\\\\", "_");
    }

    private boolean lineIsCorrect(Row row, boolean supplierRBT) throws NullPointerException {
        String firstCell;
        if (row.getCell(0) != null) {
            firstCell = row.getCell(0).getStringCellValue();
        }
        else return false;

        if (supplierRBT) {
            return (!firstCell.isEmpty() && !firstCell.startsWith("8(351)") && !firstCell.startsWith(".") && !firstCell.startsWith("г. Челябинск") && !firstCell.startsWith("Код товара"));
        }
        return (!firstCell.isEmpty() && !StringUtils.containsIgnoreCase(firstCell, "Уценка") && !firstCell.contains("Группа 1"));
    }

    /*Обратока вычисляемых значений для товара*/
    private Product matchProductJSON(Map.Entry<String, String> aliasEntry, Product product) {
        try
        {
            String[] productDetails = aliasEntry.getValue().split(",");

            Double defaultCoefficient   = Double.valueOf(productDetails[1]); /// resolveCoefficient() если product с уникальной ценой
            String productGroup         = productDetails[0];
            String singleTypeName       = productDetails[2];
            String productCategory      = productDetails[3];

            product.setProductCategory(productCategory);
            product.setProductGroup(productGroup);
            product.setDefaultCoefficient(defaultCoefficient);
            product.setSingleTypeName(singleTypeName);
            product.setMappedJSON(true);
            return product;
        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
        }
        return product;
    }
    private Product defaultProductMatch(OriginalProduct originalProduct, Product product) {
        Double defaultCoefficient = 1.2;
        String defaultCategory      = resolveDefaultCategory(originalProduct.getOriginalCategory());
        String defaultProductGroup  = resolveDefaultGroup(originalProduct.getOriginalType());//originalProduct.getOriginalType();
        String singleTypeName       = StringUtils.substringBefore(originalProduct.getOriginalName().toLowerCase(), originalProduct.getOriginalBrand().toLowerCase());

        product.setProductCategory(defaultCategory);
        product.setProductGroup(defaultProductGroup);
        product.setSingleTypeName(singleTypeName);
        product.setDefaultCoefficient(defaultCoefficient);
        return product;
    }

    private String resolveDefaultGroup(String originalType) {
        if (originalType.contains("_")) {
            originalType = StringUtils.substringAfter(originalType, "_");
        }
        return originalType;
    }

    private String resolveDefaultCategory(String originalCategory) {
        if (originalCategory.contains("_")) {
            originalCategory = StringUtils.substringAfter(originalCategory, "_");
        }

        switch (originalCategory) {
            case "Аудио": return "Автотовары";
            case "Электроника": return "Теле-Видео-Аудио";
            case "Крупная техника для кухни":
            case "Мелкая техника для кухни": return "Кухонная техника";
            case "Красота и здоровье":return "Приборы персонального ухода";
            case "Инструменты для дома, дачи и авто":
            case "Инструмент": return "Строительные инструменты";
            case "Гаджеты": return "Цифровые устройства";
            case "Отдых и Развлечения" : return "Спорт и отдых";
            default: return originalCategory;
        }
    }

    private String resolveShortModel(String modelName, String singleTypeName, String originalBrand) {
        Pattern pattern = Pattern.compile("^[\\w\\W\\s?]+ [А-ЯA-Z\\W?]{3,20}$");
        Matcher matcher = pattern.matcher(modelName);

        String shortModelName;

        if (matcher.matches()) {
            shortModelName = StringUtils.substringBeforeLast(modelName, " ").replaceAll(" ", "");
        }
        else shortModelName = modelName.replaceAll(" ", "");

        return shortModelName.replaceAll("-", "").replaceAll("_", "").replaceAll("_", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", "").toLowerCase();
    }

    private String formatAnnotation(String annotation) {
        if (!annotation.isEmpty() && annotation.contains(";")) {
            return annotation.replaceAll(";", "<br>");
        }
        return null;
    }

    private String resolveAnnotation(OriginalProduct originalProduct, boolean supplierRBT) {
        String annotation;
        if (supplierRBT) {
            annotation = originalProduct.getOriginalAnnotation();
            return !annotation.isEmpty() ? annotation : originalProduct.getOriginalName();
        }
        annotation = StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");
        return !annotation.isEmpty() ? annotation : originalProduct.getOriginalName();
    }

    private String resolveSearchName(String modelName, String singleTypeName, String originalBrand) {
        String shortModel;
        Pattern pattern = Pattern.compile("^[\\w\\W\\s?]+ [А-ЯA-Z\\W?]{3,20}$");
        Matcher matcher = pattern.matcher(modelName);

        if (matcher.matches()) {
            shortModel = StringUtils.substringBeforeLast(modelName, " ").replaceAll(" ", "");
        }
        else {
            shortModel = modelName.replaceAll(" ", "");
        }

        shortModel = shortModel.replaceAll("-", "").replaceAll("_", "").replaceAll("_", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", "").toLowerCase();
        return singleTypeName.concat(originalBrand).concat(shortModel).replaceAll(" ", "").toLowerCase();
    }

    private String resolveBrandName(String singleTypeName, String originalBrand) {
        return singleTypeName.concat(" ").concat(StringUtils.capitalize(originalBrand.toLowerCase()));
    }

    private Integer resolveBonus(Integer finalPrice) throws NullPointerException {
        int bonus = finalPrice * 3 / 100;
        String bonusToRound = String.valueOf(bonus);

        if (bonus > 0 && bonus <= 10) {
            return 10;
        }
        else {
            bonusToRound = bonusToRound.substring(0, bonusToRound.length()-1).concat("0");
            return Integer.parseInt(bonusToRound);
        }
    }

    private Integer resolveFinalPrice(OriginalProduct originalProduct, Double coefficient) throws NumberFormatException {
        if (productWithUniquePrice(originalProduct)) {
            return Integer.parseInt(StringUtils.deleteWhitespace(brandsRepo.findByProductID(originalProduct.getProductID()).getFinalPrice()));
        }
        /// else if (productPriceModified())
        else return makeRoundFinalPrice(originalProduct.getOriginalPrice(), coefficient);
    }

    private boolean productWithUniquePrice(OriginalProduct originalProduct) {
        String originalBrand = originalProduct.getOriginalBrand().toUpperCase();
        String[] uniqueBrands = {"ARDIN","SENTORE","BINATONE","AMCV","DOFFLER","DOFFLER PLUS","EXCOMP","LERAN"};

        if (Arrays.asList(uniqueBrands).contains(originalBrand)) {
            return brandsRepo.findByProductID(originalProduct.getProductID()) != null;
        }
        return false;
    }

    private Integer makeRoundFinalPrice(String originalPrice, Double coefficient) {
        try
        {
            int finalPrice = (int) (Double.parseDouble(originalPrice) * coefficient);
            String finalPriceToRound = String.valueOf(finalPrice);

            if (finalPrice > 0 && finalPrice <= 10) {
                return 10;
            }
            else if (finalPrice > 10 && finalPrice < 1000) {
                finalPriceToRound = finalPriceToRound.substring(0, finalPriceToRound.length()-1).concat("9");
                return Integer.parseInt(finalPriceToRound);
            }
            else if (finalPrice > 1000) {
                finalPriceToRound = finalPriceToRound.substring(0, finalPriceToRound.length()-2).concat("90");
                return Integer.parseInt(finalPriceToRound);
            }
            else return finalPrice;
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return (int) (Double.parseDouble(originalPrice) * 1.2);
    }

    private String resolveFullName(String originalName, String modelName, String singleTypeName, String originalBrand) {
        String fullName = singleTypeName.concat(" ").concat(StringUtils.capitalize(originalBrand.toLowerCase())).concat(" ").concat(modelName);
        return !fullName.isEmpty() ? fullName : StringUtils.substringBefore(originalName, ", ");
    }

    private String resolveModelName(OriginalProduct originalProduct) {
        String originalBrand = originalProduct.getOriginalBrand().toLowerCase();
        String originalName = originalProduct.getOriginalName().toLowerCase();
        String modelName;

        if (originalProduct.getSupplier().equals("RBT") && !originalBrand.isEmpty()) {
            modelName = StringUtils.substringAfter(originalProduct.getOriginalName().toLowerCase(), originalBrand.toUpperCase().replaceAll(" ", "").replaceAll("&", "")).trim();
        }
        else if (originalName.contains(", ") && originalName.contains(originalBrand) && StringUtils.substringAfter(originalName, originalBrand).contains(",")) {
            modelName = StringUtils.substringBetween(originalName, originalBrand, ",").trim();
        }
        else modelName = StringUtils.substringAfter(originalName, originalBrand).trim();

        if (!modelName.isEmpty()) {
            return modelName;
        }
        else if (originalName.contains(originalBrand)) {
            return StringUtils.substringAfter(originalName, originalBrand);
        }
        else return "No modelName";
    }

    /// Оптимизировать скорость!
    private void resolveDuplicates() {
        log.info("Обработка дубликатов...");
        int count = 0;

        /// delete
        productRepo.findAll().forEach(product -> {
            product.setIsDuplicate(null);
            product.setHasDuplicates(null);
            productRepo.save(product);
        });

        List<Product> products = productRepo.findBySupplier("RBT");
        for (Product product : products)
        {
            /*product.setIsDuplicate(false);
            product.setHasDuplicates(false);*/

            String shortModel = product.getShortModelName();
            List<Product> duplicates = productRepo.findBySupplierAndShortModelNameIgnoreCase("RUSBT", shortModel);

            if (!duplicates.isEmpty())
            {
                duplicates.sort(Comparator.comparing(Product::getFinalPrice));
                for (Product duplicateProduct : duplicates)
                {
                    if (product.getFinalPrice() <= duplicateProduct.getFinalPrice()) {
                        product.setHasDuplicates(true);
                        duplicateProduct.setIsDuplicate(true);
                    }
                    else {
                        product.setIsDuplicate(true);
                        duplicateProduct.setHasDuplicates(true);
                        //duplicateProduct.setPic(product.getPic());
                        //duplicateProduct.setAnnotation(product.getAnnotation());
                    }
                    productRepo.save(product);
                    productRepo.save(duplicateProduct);
                }
                count++;
            }
            /*else productRepo.save(product);*/
        }
        log.info("Товаров с дубликатами: " + count);
    }

    private void resolveAvailable() {
        log.info("Resolving available...");
        productRepo.findAll().forEach(product -> {
            if (!product.getUpdateDate().toString().equals(LocalDate.now().toString())) {
                productRepo.delete(product);
            }
        });
    }

    public void updateBrandsPrice(MultipartFile file) {
        log.info(file.getOriginalFilename());
        try
        {
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())), ';');

            for (String[] line: reader)
            {
                if (!line[0].isEmpty()) {
                    log.info(line[0]);

                    UniqueBrand brandProduct = brandsRepo.findByProductID(line[0]);

                    if (brandProduct == null) {
                        brandProduct = new UniqueBrand();
                        brandProduct.setProductID(line[0]);
                    }

                    brandProduct.setFullName(line[1]);
                    brandProduct.setBrand(line[2]);
                    brandProduct.setAnnotation(line[3]);
                    brandProduct.setOriginalPrice(line[4]);
                    brandProduct.setFinalPrice(line[5]);
                    brandProduct.setPercent(line[6]);

                    brandsRepo.save(brandProduct);
                    log.info(brandProduct.getFullName());
                }
            }
        }
        catch (IOException | ArrayIndexOutOfBoundsException exp) {
            exp.getStackTrace();
        }
    }

    /*private String resolveFullName(OriginalProduct originalProduct) {
        String name = originalProduct.getOriginalName();

        if (name.matches("^[а-яА-Я]*\\s[0-9]{1,2}([,][1-9]{1,2})\\s.*$")) {
            log.info(name);
            return StringUtils.substringBefore(name, ", ");
        }
        return StringUtils.substringBefore(name, ",");
    }*/

    public void test() {
        /*for (Product product : productRepo.findAll()) {
            if (product.getProductCategory().contains("_")) {
                product.setProductCategory(StringUtils.substringAfter(product.getProductCategory(), "_"));
                productRepo.save(product);
            }
        }*/

        /*for (OriginalProduct product : originalRepo.findAll()) {
            product.setUpdateDate(LocalDate.ofYearDay(2019, 63));
            originalRepo.save(product);
        }

        for (Product product : productRepo.findAll()) {
            product.setUpdateDate(LocalDate.ofYearDay(2019, 63));
            productRepo.save(product);
        }*/

        String[] notParams = {": нет", ": 0",  ": -", "количество шт в"};

        for (OriginalProduct product : originalRepo.findAll().stream().filter(originalProduct -> !originalProduct.getOriginalAnnotation().isEmpty()).collect(Collectors.toList())) {
            String splitter  = product.getSupplier().equals("RBT") ? "; " : ", ";
            //List<String> filters = Arrays.asList(product.getOriginalAnnotation().split(splitter));

            List<String> filters = new LinkedList<>(Arrays.asList(product.getOriginalAnnotation().split(splitter)));


            System.out.println();
            log.info(filters.toString());

            filters.removeIf(filter -> Arrays.stream(notParams).parallel().anyMatch(filter::contains));


            /*filters.forEach(filter -> {
                boolean stop = Arrays.stream(notParams).parallel().anyMatch(filter::contains);


                *//*if (stop) {
                    log.info(filter);
                    filters.remove(filter);
                    //ArrayUtils.remove(filters, filter);

                }*//*
            });*/

            /*for (String filter : filters) {
                boolean stop = Arrays.stream(notParams).parallel().anyMatch(filter::contains);
                if (stop) {
                    log.info(filter);
                    filters.remove(filter);
                    //ArrayUtils.remove(filters, filter);

                }
            }*/
            log.info(filters.toString());
        }



        /*Set<String> categories = new TreeSet<>();
        productRepo.findAll().forEach(product -> categories.add(product.getProductCategory()));
        categories.forEach(log::info);

        Set<String> groups = new TreeSet<>();
        productRepo.findByProductCategoryIgnoreCase("Отдых и Развлечения").forEach(product -> groups.add(product.getProductGroup()));
        System.out.println();
        groups.forEach(log::info);*/



        /*og.info("test");

        for (OriginalProduct originalProduct : originalRepo.findAll())
        {
            String originalBrand = originalProduct.getOriginalBrand().toLowerCase();
            String originalName = originalProduct.getOriginalName().toLowerCase();


            *//*if (originalProduct.getSupplier().equals("RBT") && !originalBrand.isEmpty()) {
                String case1 = StringUtils.substringAfter(originalProduct.getOriginalName().toLowerCase(), originalBrand.toUpperCase().replaceAll(" ", "").replaceAll("&", "")).trim();
                log.info("case1 " + case1);
            }
            else if (originalName.contains(", ") && originalName.contains(originalBrand) && StringUtils.substringAfter(originalName, originalBrand).contains(","))
            {
                String case1 = StringUtils.substringBetween(originalName, originalBrand, ",").trim();
                log.info("case1 " + case1);
            }*//*
            String modelName;

            if (originalProduct.getSupplier().equals("RBT") && !originalBrand.isEmpty()) {
                modelName = StringUtils.substringAfter(originalProduct.getOriginalName().toLowerCase(), originalBrand.toUpperCase().replaceAll(" ", "").replaceAll("&", "")).trim();
                *//*if ()
                System.out.println();
                log.info("case1, RBT");
                log.info(originalProduct.getOriginalName());
                log.info("modelName: " + modelName);*//*

            }
            else if (originalName.contains(", ") && originalName.contains(originalBrand) && StringUtils.substringAfter(originalName, originalBrand).contains(",")) {
                modelName = StringUtils.substringBetween(originalName, originalBrand, ",").trim();
                *//*System.out.println();
                log.info("case2, else if, substringAfter contains(,)");
                log.info(originalProduct.getOriginalName());
                log.info("case2 " + modelName);*//*
            }
            else {
                modelName = StringUtils.substringAfter(originalName, originalBrand).trim();
                *//*System.out.println();
                log.info("case3, else");
                log.info(originalProduct.getOriginalName());
                log.info("modelName: " + modelName);*//*
            }

            if (modelName.isEmpty()) {
                if (originalName.contains(originalBrand)) {
                    modelName = StringUtils.substringAfter(originalName, originalBrand);
                }
                else {
                    System.out.println();
                    log.info("OrigBrand: " + originalBrand);
                    log.info("OrigName: " + originalName);
                }
            }
        }*/

        /*// ^[\w\W\s?]+ [0-9]{1,2}([,][0-9]{1,2}) [\w\W\s?]+$
        // ^[0-9]{1,2}([,][0-9]{1,2})$
        // ^ [0-9]{1,2}([,][1-9]{1,2}) $
        // ^[^.]* [0-9]{1,2}([,][1-9]{1,2}) [^.]*$
        // ^[а-яА-Я]* [0-9]{1,2}([,][1-9]{1,2}) .*$

        log.info("lol");
        for (OriginalProduct originalProduct : originalRepo.findAll())
        {
            String name = originalProduct.getOriginalName();

            if (name.matches("^[а-яА-Я]*\\s[0-9]{1,2}([,][1-9]{1,2})\\s.*$")) {
                log.info(name);
            }

            *//*boolean find = Pattern.compile("^[0-9]{1,2}([,][0-9]{1,2})$").matcher(name).find();
            if (find) log.info(originalProduct.getOriginalName());*//*


         *//*Pattern pattern = Pattern.compile("[0-9]{1,2}([,][0-9]{1,2})");
            Matcher matcher = pattern.matcher(originalProduct.getOriginalName());

            if (matcher.matches()) {
                log.info(originalProduct.getOriginalName());
            }*//*
        }
        log.info("end");*/
    }
}


/*!!! ЧЕРЕЗ SPRING CONTEXT*/
/*
 * BEAN Matcher
 * COLLECTION Map<String, String[]> aliases
 * ЗНАЧЕНИЯ ДЛЯ КАЖДОЙ ENTRY В PROPERTIES
 * 1элемент - Синоним, 2.group, 3.coeff, 4.singleName, 5.category
 * В итерации для каждого originalProduct из таблицы Поставщиков
 * for entry : aliases {
 *   if(entry.key.startWith(originalProduct.getType)) {
 *           match new Product()
 *       }
 * }*/