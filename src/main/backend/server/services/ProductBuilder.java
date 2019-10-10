package server.services;
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
import server.repos.OriginalRepo;
import server.repos.ProductRepo;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Log
@Service
@AllArgsConstructor
public class ProductBuilder {
    private final OriginalRepo originalRepo;
    private final ProductRepo productRepo;
    private final AliasConfig aliasConfig;

    public void updateProductsDB(MultipartFile excelFile) throws FileNotFoundException {
        log.info(excelFile.getOriginalFilename());

        //LinkedHashMap<String, String> aliases = aliasConfig.aliasesMap();
        //aliases.forEach((keyArray, valArray) -> log.info(Arrays.toString(keyArray.split(",")) + " : " + Arrays.toString(valArray.split(","))));

        //parseSupplierFile(excelFile);
        matchProductDetails();
        resolveDuplicates();
        resolveAvailable();
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
            String supplier             = supplierRBT ? "RBT" : "RUSBT";
            String originalBrand;

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

        String newOriginalPrice  = supplierRBT ? row.getCell(6).toString() : row.getCell(13).toString();
        String newOriginalAmount = supplierRBT ? row.getCell(7).toString() : row.getCell(7).toString().concat(row.getCell(8).toString());

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
        List<OriginalProduct> originalProducts = originalRepo.findAll();
        LinkedHashMap<String, String> aliases = aliasConfig.aliasesMap();

        /*aliases.forEach((keyArray, valArray) -> {
            System.out.println();
            log.info(Arrays.toString(keyArray.split(",")));
            log.info(Arrays.toString(valArray.split(",")));
        });*/

        for (OriginalProduct originalProduct : originalProducts)
        {
            String originalGroup = originalProduct.getSupplier().equals("RBT") ? originalProduct.getOriginalGroup() : originalProduct.getOriginalType();
            for (Map.Entry<String,String> aliasEntry : aliases.entrySet())
            {
                for (String alias : aliasEntry.getKey().split(","))
                {
                    if (StringUtils.startsWithIgnoreCase(originalGroup, alias)) {
                       //Product product =
                       matchProduct(originalProduct, aliasEntry);
                    }
                }
            }
            //Product product = alternativeMatch(originalProduct, aliasEntry);
            /// alternativeMatch()
        }
    }

    private /*Product*/void matchProduct(OriginalProduct originalProduct, Map.Entry<String, String> aliasEntry) {
        System.out.println();
        log.info("Товар: " + originalProduct.getOriginalName());
        log.info("Alias: " + aliasEntry.getKey());
        log.info("Details: " + aliasEntry.getValue());
        //return new Product();


    }



    /*private void matchProduct(String alias, String productGroup, double coefficient, String single, String productCategory, OriginalProduct originalProduct) {
        String[] matches = alias.split(",");
        String supplier  = originalProduct.getSupplier();
        String productID = originalProduct.getProductID();

        try
        {
            for (String match : matches)
            {
                String annotation, modelName, fullName, groupBrand, productType, formattedAnnotation;

                Product product = productRepo.findProductByProductID(productID);
                if (product == null) {
                    product = new Product();
                    product.setProductID(productID);
                }

                *//**//*

            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }


    }*/

    private boolean productValidToMatch(OriginalProduct originalProduct, Product product) {
        return product.getProductGroup() == null && (!originalProduct.getOriginalBrand().isEmpty() && StringUtils.containsIgnoreCase(originalProduct.getOriginalName().replaceAll(" ", "").replaceAll("&", ""), originalProduct.getOriginalBrand().replaceAll(" ", ""))); /// В ЗАПРОС К БД

    }

    private void resolveDuplicates() {
        /**/
    }

    private void resolveAvailable() {
        /**/
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