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
import java.util.concurrent.atomic.AtomicInteger;
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
    /*#1*/
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
    /*#2*/
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
    /*#3*/
    private boolean originalProductExists(String productID) {
        return originalRepo.findByProductID(productID) != null;
    }
    /*#4*/
    private String resolveProductID(boolean supplierRBT, Row row) {
        return supplierRBT ? row.getCell(0).toString() : row.getCell(5).toString().replaceAll("\\\\", "_");
    }
    /*#5*/
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
    private void matchProductDetails() throws FileNotFoundException {
        log.info("Matching product details");

        List<OriginalProduct> originalProducts = originalRepo.findAll();
        LinkedHashMap<String, String> aliases = aliasConfig.aliasesMap();

        main: for (OriginalProduct originalProduct : originalProducts)
        {

            {   /*matchGroup()*/
                String originalGroup = originalProduct.getOriginalType();
                for (Map.Entry<String,String> aliasEntry : aliases.entrySet())
                {
                    for (String alias : aliasEntry.getKey().split(","))
                    {
                        if (StringUtils.startsWithIgnoreCase(originalGroup, alias)) {
                            matchProduct(originalProduct, aliasEntry);
                            continue main;
                        }
                    }
                }
                defaultProductMatch(originalProduct);
            }

            /*resolve details*/
        }
    }

    private void matchProduct(OriginalProduct originalProduct, Map.Entry<String, String> aliasEntry) {
        try
        {
            String productID = originalProduct.getProductID();
            String originalBrand = originalProduct.getOriginalBrand();
            boolean supplierRBT = originalProduct.getSupplier().equals("RBT");

            Product product = productRepo.findProductByProductID(productID);
            if (product == null) {
                product = new Product();
                product.setProductID(productID);
            }

            String[] productDetails = aliasEntry.getValue().split(",");

            Double defaultCoefficient   = Double.valueOf(productDetails[1]); /// resolveCoefficient() если product с уникальной ценой
            String productGroup         = productDetails[0];
            String singleTypeName       = productDetails[2];
            String productCategory      = productDetails[3];

            Integer finalPrice    = resolveFinalPrice(originalProduct, defaultCoefficient);
            Integer bonus         = resolveBonus(finalPrice);
            String modelName      = resolveModelName(originalProduct);
            String fullName       = resolveFullName(modelName, singleTypeName, originalBrand);
            String groupBrandName = resolveBrandName(singleTypeName, originalBrand);
            String searchName     = resolveSearchName(modelName, singleTypeName, originalBrand);
            String shortModelName = resolveShortModel(modelName, singleTypeName, originalBrand);
            String annotation     = !supplierRBT ? resolveAnnotation(originalProduct) : originalProduct.getOriginalAnnotation();

            //String formattedAnnotation  = formatAnnotation(annotation);
            //String productType     = originalProduct.getOriginalType();

            product.setProductCategory(productCategory);
            product.setProductGroup(productGroup);
            //product.setProductType(productType);
            product.setDefaultCoefficient(defaultCoefficient);
            product.setFinalPrice(finalPrice);
            product.setBonus(bonus);
            product.setSingleTypeName(singleTypeName);
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
            productRepo.save(product);


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
            log.info("finalPrice: "         + finalPrice);
            log.info("bonus: "              + bonus);
            /**/
            log.info("singleTypeName: " + singleTypeName);
            log.info("modelName: "      + modelName);
            log.info("fullName: "       + fullName);
            log.info("groupBrandName: " + groupBrandName);
            log.info("searchName: "     + searchName);
            /**/
            log.info("annotation: "             + annotation);
            //log.info("formattedAnnotation: "    + formattedAnnotation);

        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void defaultProductMatch(OriginalProduct originalProduct) {
        String productID     = originalProduct.getProductID();
        String supplier      = originalProduct.getSupplier().equals("RBT") ? "RBT" : "RUSBT";
        Integer finalPrice   = resolveFinalPrice(originalProduct, 1.2);
        Integer bonus        = resolveBonus(finalPrice);

        String defaultBrand = originalProduct.getOriginalBrand();
        String defaultName  = StringUtils.substringBefore(originalProduct.getOriginalName(), ", ");//resolveFullName(originalProduct);
        String defaultAnnotation = StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");;
        String modelName = resolveModelName(originalProduct);

        //String fullName      = resolveFullName(modelName, singleTypeName, originalBrand);
        ///String shortModelName = resolveShortModel(modelName, singleTypeName, originalBrand);
        /*someOtherFields*/

        Product product = productRepo.findProductByProductID(productID);
        if (product == null) {
            product = new Product();
            product.setProductID(productID);
        }

        product.setProductCategory(originalProduct.getOriginalCategory());
        product.setProductGroup(originalProduct.getOriginalType());
        product.setBrand(defaultBrand);
        product.setFinalPrice(finalPrice);
        product.setBonus(bonus);
        product.setSupplier(supplier);
        product.setModelName(modelName);
        product.setFullName(defaultName);
        product.setAnnotation(defaultAnnotation);
        /*someOtherDetails*/

        product.setUpdateDate(LocalDate.now());
        productRepo.save(product);
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

    private String resolveAnnotation(OriginalProduct originalProduct) {
        return StringUtils.substringAfter(originalProduct.getOriginalName(), ", ");
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

    private String resolveFullName(String modelName, String singleTypeName, String originalBrand) {
        return singleTypeName.concat(" ").concat(StringUtils.capitalize(originalBrand.toLowerCase())).concat(" ").concat(modelName);
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
        /*productRepo.findBySupplier("RBT").forEach(product -> {
            product.setIsDuplicate(null);
            product.setHasDuplicates(null);
            productRepo.save(product);
        });*/

        List<Product> products = productRepo.findBySupplier("RBT");
        //AtomicInteger count = new AtomicInteger();
        for (Product product : products)
        {
            //product.get
            product.setIsDuplicate(null);
            product.setHasDuplicates(null);

            List<Product> duplicates = productRepo.findBySupplier("RUSBT");

            if (!duplicates.isEmpty())
                for (Product productRUS : duplicates) {

                }

            /*List<Product> duplicates = productRepo.findByShortModelEquals(product.getShortModel());
            if (duplicates.size() > 1)
            {
                duplicates = duplicates.stream().filter(duplicate -> duplicate.getSupplier().equals("2RUS-BT")).collect(Collectors.toList());
                if (duplicates.size() >= 1)
                {
                    duplicates.sort(Comparator.comparing(Product::getFinalPrice));
                    count.getAndIncrement();

                    Product productOriginal = duplicates.stream().findFirst().get();
                    if (product.getFinalPrice() > productOriginal.getFinalPrice())
                    {
                        productOriginal.setFormattedAnnotation(product.getFormattedAnnotation());
                        productOriginal.setOriginalPic(product.getOriginalPic());
                        productOriginal.setHasDuplicates(true);
                        product.setIsDuplicate(true);
                        productRepo.save(product);
                        productRepo.save(productOriginal);

                        duplicates.stream().skip(1).forEach(duplicate ->
                        {
                            duplicate.setIsDuplicate(true);
                            productRepo.save(duplicate);
                        });
                    }
                    else
                    {
                        product.setHasDuplicates(true);
                        duplicates.forEach(duplicate ->
                        {
                            duplicate.setIsDuplicate(true);
                            productRepo.save(duplicate);
                        });
                    }
                }
            }*/
        }
        //
        //
        //
        //
        // log.info("Товаров с дубликатами: " + count.toString());
    }

    private void resolveAvailable() {
        /**/
    }

    public void updateBrandsPrice(MultipartFile file) {
        log.info(file.getOriginalFilename());
        try
        {
            //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
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

                    //String shortModel = StringUtils.substringAfter(brandProduct.getFullName().toLowerCase(), brandProduct.getBrand().toLowerCase()).replaceAll(" ", "").toLowerCase();
                    //shortModel = brandProduct.getBrand().toLowerCase().concat(shortModel).replaceAll("\\W", "");
                    //brandProduct.setShortModel(shortModel);

                    brandsRepo.save(brandProduct);
                    log.info(brandProduct.getFullName());
                    //log.info(brandProduct.getShortModel());
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