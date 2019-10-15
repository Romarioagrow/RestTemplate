package server.services;
import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
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

        log.info("Update Complete");
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
                /// XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("D:\\IT\\Dev\\ExpertStoreDev\\Prices\\" + excelFile.getOriginalFilename())));
                File file = new File("D:\\IT\\Dev\\ExpertStoreDev\\Prices\\" + excelFile.getOriginalFilename());
                FileInputStream inputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
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
        log.info("Matching product details");

        List<OriginalProduct> originalProducts = originalRepo.findAll();
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
                        product = matchProductJSON(originalProduct, aliasEntry, product);
                        break mapping;
                    }
                }
            }
            if (product.getProductGroup() == null ) {
                product = defaultProductMatch(originalProduct, product);
            }
            product = resolveProductsDetails(originalProduct, product);
            productRepo.save(product);
        }
    }

    private Product resolveProductsDetails(OriginalProduct originalProduct, Product product) {
        String singleTypeName = product.getSingleTypeName();
        String originalBrand  = originalProduct.getOriginalBrand();
        String originalName   = originalProduct.getOriginalName();
        boolean supplierRBT   = originalProduct.getSupplier().equals("RBT");

        Integer finalPrice    = resolveFinalPrice(originalProduct, product.getDefaultCoefficient());
        String modelName      = resolveModelName(originalProduct).toUpperCase().trim();
        String annotation     = resolveAnnotation(originalProduct, supplierRBT);

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

        product.setSupplier(originalProduct.getSupplier());
        product.setPic(originalProduct.getOriginalPicLink());
        product.setBrand(originalBrand);
        product.setUpdateDate(LocalDate.now());
        return product;

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
    /////////////

    private Product matchProductJSON(OriginalProduct originalProduct, Map.Entry<String, String> aliasEntry, Product product) {
        try
        {
            /*String productID = originalProduct.getProductID();*/
            String originalBrand = originalProduct.getOriginalBrand();
            boolean supplierRBT = originalProduct.getSupplier().equals("RBT");

            /*Product product = productRepo.findProductByProductID(productID);
            if (product == null) {
                product = new Product();
                product.setProductID(productID);
            }*/

            String[] productDetails = aliasEntry.getValue().split(",");

            Double defaultCoefficient   = Double.valueOf(productDetails[1]); /// resolveCoefficient() если product с уникальной ценой
            String productGroup         = productDetails[0];
            String singleTypeName       = productDetails[2];
            String productCategory      = productDetails[3];

            /*Integer finalPrice    = resolveFinalPrice(originalProduct, defaultCoefficient);
            Integer bonus         = resolveBonus(finalPrice);
            String modelName      = resolveModelName(originalProduct);
            String fullName       = resolveFullName(modelName, singleTypeName, originalBrand);
            String groupBrandName = resolveBrandName(singleTypeName, originalBrand);
            String searchName     = resolveSearchName(modelName, singleTypeName, originalBrand);
            String shortModelName = resolveShortModel(modelName, singleTypeName, originalBrand);
            String annotation     = !supplierRBT ? resolveAnnotation(originalProduct) : originalProduct.getOriginalAnnotation();*/

            //String formattedAnnotation  = formatAnnotation(annotation);
            //String productType     = originalProduct.getOriginalType();

            product.setProductCategory(productCategory);
            product.setProductGroup(productGroup);
            product.setDefaultCoefficient(defaultCoefficient);
            product.setSingleTypeName(singleTypeName);

            //product.setProductType(productType);


            /*product.setFinalPrice(finalPrice);
            product.setBonus(bonus);
            product.setModelName(modelName);
            product.setFullName(fullName);
            product.setGroupBrandName(groupBrandName);
            product.setSearchName(searchName);
            product.setShortModelName(shortModelName);
            product.setAnnotation(annotation);*/

            /*product.setSupplier(originalProduct.getSupplier());
            product.setPic(originalProduct.getOriginalPicLink());
            product.setBrand(originalBrand);
            product.setUpdateDate(LocalDate.now());*/
            //productRepo.save(product);


            System.out.println();
            log.info("Товар от поставщика");
            log.info("originalName: "      + originalProduct.getOriginalName());
            log.info("originalPrice: "      + originalProduct.getOriginalPrice());
            log.info("originalType: "      + originalProduct.getOriginalType());
            log.info( "/**/");
            /**/
            log.info("Разметка");
            log.info("Alias: "      + aliasEntry.getKey());
            log.info("Details: "    + aliasEntry.getValue());
            log.info( "/**/");
            /**/
            log.info("productCategory: "    + productCategory);
            log.info("productGroup: "       + productGroup);
            //log.info("productType: "        + productType);
            /**/
            log.info("defaultCoefficient: " + defaultCoefficient);
            /*log.info("finalPrice: "         + finalPrice);
            log.info("bonus: "              + bonus);*/
            /**/
            log.info("singleTypeName: " + singleTypeName);
            /*log.info("modelName: "      + modelName);
            log.info("fullName: "       + fullName);
            log.info("groupBrandName: " + groupBrandName);
            log.info("searchName: "     + searchName);*/
            /**/
            //log.info("annotation: "             + annotation);
            //log.info("formattedAnnotation: "    + formattedAnnotation);

            return product;
        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
        }
        return product;
    }
    private Product defaultProductMatch(OriginalProduct originalProduct, Product product) {
        String productID     = originalProduct.getProductID();

        //String supplier      = originalProduct.getSupplier().equals("RBT") ? "RBT" : "RUSBT";
        //Integer finalPrice   = resolveFinalPrice(originalProduct, 1.2);
        //Integer bonus        = resolveBonus(finalPrice);

        //String defaultBrand = originalProduct.getOriginalBrand();
        //String defaultName  = StringUtils.substringBefore(originalProduct.getOriginalName(), ", ");//resolveFullName(originalProduct);
        //String defaultAnnotation = StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");;
        //String modelName = resolveModelName(originalProduct);

        //String fullName      = resolveFullName(modelName, singleTypeName, originalBrand);
        ///String shortModelName = resolveShortModel(modelName, singleTypeName, originalBrand);
        /*someOtherFields*/

        /*Product product = productRepo.findProductByProductID(productID);
        if (product == null) {
            product = new Product();
            product.setProductID(productID);
        }*/

        Double defaultCoefficient = 1.2;
        String defaultCategory      = originalProduct.getOriginalCategory();
        String defaultProductGroup  = originalProduct.getOriginalType();
        String singleTypeName       = StringUtils.substringBefore(originalProduct.getOriginalName().toLowerCase(), originalProduct.getOriginalBrand().toLowerCase());

        product.setProductCategory(defaultCategory);
        product.setProductGroup(defaultProductGroup);
        product.setSingleTypeName(singleTypeName);
        product.setDefaultCoefficient(defaultCoefficient);
        return product;
        /*product.setBrand(defaultBrand);
        product.setFinalPrice(finalPrice);
        product.setBonus(bonus);
        product.setSupplier(supplier);
        product.setModelName(modelName);
        product.setFullName(defaultName);
        product.setAnnotation(defaultAnnotation);*/
        /*someOtherDetails*/

        /*product.setUpdateDate(LocalDate.now());
        productRepo.save(product);*/
    }

    private String resolveFullName(OriginalProduct originalProduct) {
        //return null;
        //^[0-9]{1,2}([,][0-9]{1,2})$

        /*Pattern pattern = Pattern.compile("^[0-9]{1,2}([,][0-9]{1,2})$");
        Matcher matcher = pattern.matcher(originalProduct.getOriginalName());

        if (matcher.find()) {
            log.info(originalProduct.getOriginalName());
        }
        return null;*/

        /*for (OriginalProduct originalProduct : originalRepo.findAll())
        {*/
        String name = originalProduct.getOriginalName();

        if (name.matches("^[а-яА-Я]*\\s[0-9]{1,2}([,][1-9]{1,2})\\s.*$")) {
            log.info(name);
            return StringUtils.substringBefore(name, ", ");
        }
        return StringUtils.substringBefore(name, ",");

            /*boolean find = Pattern.compile("^[0-9]{1,2}([,][0-9]{1,2})$").matcher(name).find();
            if (find) log.info(originalProduct.getOriginalName());*/


            /*Pattern pattern = Pattern.compile("[0-9]{1,2}([,][0-9]{1,2})");
            Matcher matcher = pattern.matcher(originalProduct.getOriginalName());

            if (matcher.matches()) {
                log.info(originalProduct.getOriginalName());
            }*/
        //}

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
        if (supplierRBT) {
            return StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");
        }
        String annotation = StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");
        return !annotation.isEmpty() ? annotation : StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");
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

    private Integer resolveFinalPrice(OriginalProduct originalProduct, Double coefficient) {
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

    private Integer makeRoundFinalPrice(String originalPrice, Double coefficient) throws NumberFormatException{
        /// round price до ближайшего числа с 90 на конце
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

    private String resolveFullName(String originalName, String modelName, String singleTypeName, String originalBrand) {
        String fullName = singleTypeName.concat(" ").concat(StringUtils.capitalize(originalBrand.toLowerCase())).concat(" ").concat(modelName);
        return !fullName.isEmpty() ? fullName : StringUtils.substringBefore(originalName, ", ");
    }

    private String resolveModelName(OriginalProduct originalProduct) {
        /*String originalBrand = originalProduct.getOriginalBrand();
        String originalName = originalProduct.getOriginalName();

        if (supplierRBT && !originalBrand.isEmpty()) {
            return StringUtils.substringAfter(originalProduct.getOriginalName().toLowerCase(), originalBrand.toUpperCase().replaceAll(" ", "").replaceAll("&", "")).trim();
        }
        else if (originalName.contains(", ") && originalName.contains(originalBrand) && StringUtils.substringAfter(originalName, originalBrand).contains(",")) {
            return StringUtils.substringBetween(originalName, originalBrand, ",").trim();
        }
        else return StringUtils.substringAfter(originalName, originalBrand).trim();*/

        /*for (OriginalProduct originalProduct : originalRepo.findAll())
        {*/
        String originalBrand = originalProduct.getOriginalBrand().toLowerCase();
        String originalName = originalProduct.getOriginalName().toLowerCase();


            /*if (originalProduct.getSupplier().equals("RBT") && !originalBrand.isEmpty()) {
                String case1 = StringUtils.substringAfter(originalProduct.getOriginalName().toLowerCase(), originalBrand.toUpperCase().replaceAll(" ", "").replaceAll("&", "")).trim();
                log.info("case1 " + case1);
            }
            else if (originalName.contains(", ") && originalName.contains(originalBrand) && StringUtils.substringAfter(originalName, originalBrand).contains(","))
            {
                String case1 = StringUtils.substringBetween(originalName, originalBrand, ",").trim();
                log.info("case1 " + case1);
            }*/
        String modelName;

        if (originalProduct.getSupplier().equals("RBT") && !originalBrand.isEmpty()) {
            modelName = StringUtils.substringAfter(originalProduct.getOriginalName().toLowerCase(), originalBrand.toUpperCase().replaceAll(" ", "").replaceAll("&", "")).trim();
                /*if ()
                System.out.println();
                log.info("case1, RBT");
                log.info(originalProduct.getOriginalName());
                log.info("modelName: " + modelName);*/

        }
        else if (originalName.contains(", ") && originalName.contains(originalBrand) && StringUtils.substringAfter(originalName, originalBrand).contains(",")) {
            modelName = StringUtils.substringBetween(originalName, originalBrand, ",").trim();
                /*System.out.println();
                log.info("case2, else if, substringAfter contains(,)");
                log.info(originalProduct.getOriginalName());
                log.info("case2 " + modelName);*/
        }
        else {
            modelName = StringUtils.substringAfter(originalName, originalBrand).trim();
                /*System.out.println();
                log.info("case3, else");
                log.info(originalProduct.getOriginalName());
                log.info("modelName: " + modelName);*/
        }

        if (!modelName.isEmpty()) {
            return modelName;
            /* if (originalName.contains(originalBrand)) {
                modelName = StringUtils.substringAfter(originalName, originalBrand);
            }
            else {
                System.out.println();
                log.info("OrigBrand: " + originalBrand);
                log.info("OrigName: " + originalName);
            }*/
        }
        else if (originalName.contains(originalBrand)) {
            return StringUtils.substringAfter(originalName, originalBrand);
        }
        else return "No modelName";
        //}
    }

    /*private boolean productValidToMatch(OriginalProduct originalProduct, Product product) {
        return product.getProductGroup() == null && (!originalProduct.getOriginalBrand().isEmpty() && StringUtils.containsIgnoreCase(originalProduct.getOriginalName().replaceAll(" ", "").replaceAll("&", ""), originalProduct.getOriginalBrand().replaceAll(" ", ""))); /// В ЗАПРОС К БД
    }*/

    private void resolveDuplicates() {
        System.out.println();
        log.info("Обработка дубликатов...");

        productRepo.findAll().forEach(product -> {
            product.setIsDuplicate(null);
            product.setHasDuplicates(null);
            productRepo.save(product);
        });

        List<Product> products = productRepo.findBySupplier("RBT");
        for (Product product : products)
        {
            /*product.setIsDuplicate(null);
            product.setHasDuplicates(null);*/

            String shortModel = product.getShortModelName();
            List<Product> duplicates = productRepo.findBySupplierAndShortModelNameIgnoreCase("RUSBT", shortModel);

            if (!duplicates.isEmpty())
            {
                duplicates.sort(Comparator.comparing(Product::getFinalPrice));
                for (Product duplicateProduct : duplicates) {
                    if (product.getFinalPrice() <= duplicateProduct.getFinalPrice()) {
                        product.setHasDuplicates(true);
                        duplicateProduct.setIsDuplicate(true);
                    }
                    else {
                        product.setIsDuplicate(true);
                        duplicateProduct.setHasDuplicates(true);
                        duplicateProduct.setPic(product.getPic());
                        duplicateProduct.setAnnotation(product.getAnnotation());
                    }
                    productRepo.save(product);
                    productRepo.save(duplicateProduct);
                }
            }
        }
    }

    private void resolveAvailable() {
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

    public void test() {
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
    }
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