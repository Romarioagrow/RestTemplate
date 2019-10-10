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

        LinkedHashMap<String, String> aliases = aliasConfig.aliasesMap();
        aliases.forEach((keyArray, valArray) -> log.info(Arrays.toString(keyArray.split(",")) + " : " + Arrays.toString(valArray.split(","))));

        /*parseSupplierFile(excelFile);
        matchProductDetails();
        resolveDuplicates();
        resolveAvailable();*/
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
    private void matchProductDetails() {
        List<OriginalProduct> originalProducts = originalRepo.findAll();

        for (OriginalProduct product : originalProducts)
        {
            /*//String productID = originalProduct.getProductID();

             *//*Product product = productRepo.findProductByProductID(productID);
            if (product == null) {
                product = new Product();
                product.setProductID(productID);
            }*//*

            //if (productValidToMatch(originalProduct, product))
            {
                System.out.println();
                log.info(originalProduct.toString());
                log.info("//////////");
                //log.info(product.toString());

            }*/

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

            /*АВТОТОВАРЫ*/
            /*matchProduct("10.01,Акустика"									                                , "Автоакустика"						,1.18	            , "Автоакустика"						, "Автотовары"					, product);
            matchProduct("10.02,Автомагнитолы"											                    , "Автомагнитолы"						,1.18	            , "Автомагнитола"						, "Автотовары"					, product);
            matchProduct("15.10.04.03,Видеорегистраторы"									                , "Видеорегистраторы"					,1.25	            , "Видеорегистратор"					, "Автотовары"					, product);
            matchProduct("15.10.04.01"													                    , "Радар детекторы"					    ,1.18	            , "Радар детектор"					    , "Автотовары"					, product);
            matchProduct("Автоусилитель"													                , "Автоусилители"						,1.18	            , "Автоусилитель"					    , "Автотовары"					, product);

            *//*ИНСТРУМЕНТЫ ДЛЯ ДОМА*//*
            matchProduct("06.06.05_Строительные"														    , "Строительные пылесосы"				,1.16	            , "Строительный пылесосы"				, "Инструменты для дома"		    , product);
            matchProduct("06.06.02_Фены промышленные"														, "Промышленные фены"					,1.16	            , "Промышленный фен"					, "Инструменты для дома"		    , product);
            matchProduct("Сверла,15.06.03.04"														        , "Сверла"							    ,1.16	            , "Сверло"						        , "Инструменты для дома"		    , product);
            matchProduct("15.06.03.08"														                , "Буры"							    ,1.16	            , "Бур"						            , "Инструменты для дома"		    , product);
            matchProduct("06.06.07_Генераторы,Электрогенераторы"										    , "Генераторы"							,1.16	            , "Генератор"						    , "Инструменты для дома"		    , product);
            matchProduct("Бензиновые цепные пилы"										                    , "Бензопилы"							,1.16	            , "Бензопила"						    , "Инструменты для дома"		    , product);
            matchProduct("Паяльники"										                                , "Паяльники"							,1.16	            , "Паяльники"						    , "Инструменты для дома"		    , product);

            matchProduct("06.01.01,06.01.02,06.01.03,06.01.05,Шуруповерты,Дрели"				            , "Дрели-Шуруповерты"					,1.16	            , "Дрель-Шуруповерт"					, "Инструменты для дома"		    , product);
            matchProduct("06.02,Шлифовальные машины"										                , "Шлифовальные машины"				    ,1.16	            , "Шлифовальная машина"				    , "Инструменты для дома"		    , product);
            matchProduct("06.06.08,Сварочные аппараты"									                    , "Сварочное оборудование"				,1.16	            , "Сварочный аппарат"					, "Инструменты для дома"		    , product);
            matchProduct("06.01.06,Перфораторы"											                , "Перфораторы"						    ,1.16	            , "Перфоратор"						    , "Инструменты для дома"		    , product);
            matchProduct("06.03.01,Электрические цепные пилы,Дисковые пилы,06.10.08"						, "Электропилы"						    ,1.16	            , "Электропила"						    , "Инструменты для дома"		    , product);
            matchProduct("06.03.02_Лобзики"														        , "Лобзики"							    ,1.16	            , "Электролобзик"						, "Инструменты для дома"		    , product);

            *//*ПРИБОРЫ ПЕРСОНАЛЬНОГО УХОДА*//*
            matchProduct("05.02.01., Плойки"										                        , "Плойки"						        ,1.22	            , "Плойки"					            , "Приборы персонального ухода"	, product);
            matchProduct("05.03.01.01, Машинки для стрижки"										        , "Машинки для стрижки"				    ,1.22	            , "Машинка для стрижки"				    , "Приборы персонального ухода"	, product);
            matchProduct("15.05.01.05 Бритвенные станки"										            , "Бритвенные станки"					,1.22	            , "Бритвенный станок"					, "Приборы персонального ухода"	, product);
            matchProduct("05.09.01.0"										                                , "Зубные щетки"						,1.22	            , "Зубная щетка"					    , "Приборы персонального ухода"	, product);
            //15.05.01.05 Бритвенные станки!(1.5)
            matchProduct("05.10.01,Весы напольные"										                    , "Напольные весы"						,1.22	             , "Напольные весы"					    , "Приборы персонального ухода"	, product);
            matchProduct("05.02.03,Наборы и стайлеры"												        , "Стайлеры"							,1.22	             , "Стайлер"							, "Приборы персонального ухода"	, product);
            matchProduct("05.01.01,Фен"													                , "Фены"								,1.22	             , "Фен"								, "Приборы персонального ухода"	, product);
            matchProduct("05.06.01.0,05.06.02_Сеточные бритвы,05.07.02_Бритвы для женщин,Бритвы роторные"	, "Бритвы"					            ,1.22	             , "Бритва"							    , "Приборы персонального ухода"	, product);
            matchProduct("05.02.02,Щипцы для выпрямления"									                , "Щипцы для волос"					    ,1.22	             , "Бритвенный станок"				    , "Приборы персонального ухода"	, product);
            matchProduct("05.03.02,Триммеры для лица "									                    , "Триммеры для бритья"				    ,1.22	             , "Триммер"							, "Приборы персонального ухода"	, product);
            matchProduct("05.07.01.02_Эпиляторы,Эпиляторы,05.07.03_Фотоэпиляторы"							, "Эпиляторы"							,1.22	             , "Эпиляторы"						    , "Приборы персонального ухода"	, product);
            matchProduct("05.08.0"															                , "Массажеры"							,1.22	             , "Массажер"							, "Приборы персонального ухода"	, product);
            matchProduct("05.11,Маникюрные наборы"														    , "Маникюрные наборы"					,1.22               , "Маникюрный набор"					, "Приборы персонального ухода"	, product);
            matchProduct("Машинки для стрижки"															    , "Машинки для стрижки"				    ,1.22               , "Машинка для стрижки"				    , "Приборы персонального ухода"	, product);

            *//*КЛИМАТИЧЕСКАЯ ТЕХНИКА*//*
            matchProduct("15.04.01.01"											                            , "Аксессуары для климатической техники",2	                , "Аксессуар для климатической техники" , "Климатическая техника"		    , product);
            matchProduct("Термометры,Термометр"											                , "Термометры"						    ,1.15	            , "Термометр"						    , "Климатическая техника"		    , product);
            matchProduct("04.01,Кондиционеры"											                    , "Кондиционеры"						,1.15	            , "Кондиционер"						    , "Климатическая техника"		    , product);
            matchProduct("04.05,Водонагреватели"											                , "Водонагреватели"					    ,1.20	            , "Водонагреватель"					    , "Климатическая техника"		    , product);
            matchProduct("04.04.01,Увлажнители"											                , "Увлажнители воздуха"				    ,1.20	            , "Увлажнитель воздуха"				    , "Климатическая техника"		    , product);
            matchProduct("04.02,Конвектор,Обогреватель,Тепловая пушка,Тепловентилятор"					    , "Обогреватели"						,1.20	            , "Обогреватель"						, "Климатическая техника"		    , product);
            matchProduct("04.03,Вентиляторы"												                , "Вентиляторы"						    ,1.20	            , "Вентилятор"						    , "Климатическая техника"		    , product);
            matchProduct("04.04.03,Мойки воздуха"										                    , "Очистители воздуха"					,1.20	            , "Очиститель воздуха"				    , "Климатическая техника"		    , product);

            *//*КОМПЬЮТЕРЫ И ОРГТЕХНИКА*//*
            //Моноблоки!
            matchProduct("15.08.01.01,15.08.01.03,Сумки и чехлы для ноутбуков"								, "Сумки для ноутбуков"				    ,2	                , "Сумка для ноутбука" 				    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.02.02,15.08.02.01,15.08.02.08"								                , "Аксессуары для ноутбуков"			,2	                , "Аксессуар для ноутбука" 			    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.02.12"								                                        , "Аккумуляторы для ноутбуков"			,2	                , "Аккумулятор для ноутбука" 			, "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.04.02,15.08.04.05"								                            , "Картриджи струйные"					,2	                , "Картридж струйный" 				    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.03.01"								                                        , "Картриджи лазерные"					,2	                , "Картриджи лазерные" 				    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.24.02.19"								                                        , "Бумага для принтеров"				,2	                , "Бумага для принтеров" 				, "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.07.01"								                                        , "Фотобумага"					        ,2	                , "Фотобумага" 						    , "Компьютеры и оргтехника"		, product);

            matchProduct("15.08.13.01"															            , "Веб камеры"						    ,1.17	            , "Веб-камера" 						    , "Компьютеры и оргтехника"		, product);
            matchProduct("08.03,Ноутбуки"															        , "Ноутбуки"							,1.17	            , "Ноутбук" 							, "Компьютеры и оргтехника"		, product);
            matchProduct("08.05,МФУ"														                , "Принтеры"							,1.17	            , "Принтер"							    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.36,Клавиатура"											                    , "Клавиатуры"							,1.30	            , "Клавиатура"						    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.37,Мышь"													                , "Мыши"								,1.30	            , "Мышь"								, "Компьютеры и оргтехника"		, product);
            matchProduct("08.04,Мониторы"												                    , "Мониторы"							,1.17	            , "Монитор"							    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.18,Флеш"													                , "Flash карты"						    ,1.40	            , "Flash карта"						    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.02.04,Сетевые фильтры"										                , "Сетевые фильтры"					    ,2	                , "Сетевой шнур"						, "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.29"														                , "Роутеры и сетевое оборудование"		,1.20	            , "Роутер"							    , "Компьютеры и оргтехника"		, product);
            matchProduct("15.08.02.04"											 		                    , "Внешние жесткие диски"				,1.18	            , "Внешний жесткий диск"				, "Компьютеры и оргтехника"		, product);
            matchProduct("08.01"														                    , "Готовые ПК"							,1.17	            , "Готовый ПК"						    , "Компьютеры и оргтехника"		, product);

            *//*ЦИФРОВЫЕ УСТРОЙСТВА*//*
            //Планшеты Apple!
            matchProduct("15.24.02.,15.24.02.03,15.09.03.10,06.04.03"			                            , "Зарядные устройства"				    ,2			        , "Зарядное устройство"				    , "Цифровые устройства"			, product);
            matchProduct("15.08.05.01,15.09.01.07,15.09.02.02"			                                    , "Сумки для техники"					,2			        , "Сумка для техники"					, "Цифровые устройства"			, product);
            matchProduct("09.06.01,09.06.02,09.06.03,09.06.04,Фотоаппараты"			                    , "Фотоаппараты"						,1.17				, "Фотоаппарат"						    , "Цифровые устройства"			, product);
            matchProduct("09.01.02,Смартфоны"											                    , "Смартфоны"							,1.13				, "Смартфон"							, "Цифровые устройства"			, product);
            matchProduct("09.01.01,Сотовые телефоны"										                , "Сотовые телефоны"					,1.18				, "Сотовый телефон"					    , "Цифровые устройства"			, product);
            matchProduct("08.02,Планшеты"												                    , "Планшеты"							,1.17				, "Планшет"							    , "Цифровые устройства"			, product);
            matchProduct("15.08.33,15.08.34,15.16,Наушники"							                    , "Наушники и гарнитуры"				,1.30				, "Наушники"							, "Цифровые устройства"			, product);
            matchProduct("15.09.03.02,Внешние аккумуляторы"								                , "Внешние аккумуляторы"				,1.18				, "Внешний аккумулятор"				    , "Цифровые устройства"			, product);
            matchProduct("15.09.03.01"								                                        , "Чехлы для телефонов"				    ,1.18				, "Чехол для телефона"				    , "Цифровые устройства"			, product);
            matchProduct("09.02"															                , "Видеокамеры"						    ,1.13				, "Видеокамера"						    , "Цифровые устройства"			, product);
            matchProduct("09.08"															                , "Электронные книги"					,1.17				, "Электронная книга"					, "Цифровые устройства"			, product);
            matchProduct("15.09.05,15.24.02.10,microSD"									                , "Карты памяти microSD"				,1.40				, "Карта памяти microSD"				, "Цифровые устройства"			, product);
            matchProduct("09.07.02"													                    , "MP3 плееры"							,1.17				, "MP3 плеер"							, "Цифровые устройства" 		    , product);
            matchProduct("09.07.01_"													                    , "Диктофоны"							,1.17				, "Диктофоны"							, "Цифровые устройства" 		    , product);
            matchProduct("15.02.29.01,Радиоприемники"													    , "Радиоприемники"						,1.17				, "Радиоприемники"					    , "Цифровые устройства" 		    , product);
            matchProduct("09.05_Радиосвязь"													            , "Рации"						        ,1.17				, "Рации"							    , "Цифровые устройства" 		    , product);

            *//*ГАДЖЕТЫ*//*
            //15.25.04.12 B-бренды(1.2)!
            matchProduct("15.25.01,Умные спортивные часы"								                    , "Умные часы"							,1.15	            , "Умные часы"						    , "Гаджеты"						, product);
            matchProduct("15.25.04,Портативная акустика"													, "Bluetooth колонки"					,1.15	            , "Bluetooth колонка"					, "Гаджеты"						, product);
            matchProduct("15.25.02.02"													                    , "VR системы"						    ,1.15	            , "VR система"						    , "Гаджеты"						, product);
            matchProduct("15.25.01.02"													                    , "Фитнес браслеты"				        ,1.15	            , "Фитнес браслет"					    , "Гаджеты"						, product);
            matchProduct("15.25.01.03"													                    , "Детские часы"					    ,1.15	            , "Детские часы"						, "Гаджеты"						, product);

            *//*ТЕХНИКА ДЛЯ ДОМА*//*
            matchProduct("Аксессуары к стиральным машинам,15.03.01.03,15.03.01.04,15.23.06.08,15.23.06.01"	, "Аксессуары к стиральным машинам"	    ,2				    , "Аксессуар к стиральным машинам"	    , "Техника для дома"			    , product);
            matchProduct("Мешок-пылесборник,15.03.03.01"	                                                , "Пылесборники для пылесосов"		    ,2				    , "Пылесборник для пылесосов"			, "Техника для дома"			    , product);
            matchProduct("HEPA-фильтр,Моторный фильтр,15.03.03.02"	                                        , "Фильтры для пылесосов"		        ,2				    , "Фильтр для пылесосов"				, "Техника для дома"			    , product);
            matchProduct("Аксессуары для пылесосов,15.03.03.03,15.03.03.04"	                            , "Аксессуары для пылесосов"		    ,2				    , "Аксессуар для пылесосов"			    , "Техника для дома"			    , product);
            matchProduct("15.03.07.03,Аксессуары для утюгов"	                                            , "Аксессуары для утюгов"		        ,2				    , "Аксессуар для утюгов"				, "Техника для дома"			    , product);
            matchProduct("15.03.09.01"	                                                                    , "Аксессуары для швейных машин"		,2				    , "ксессуар для швейных машин"		    , "Техника для дома"			    , product);

            matchProduct("03.01.02,Центрифуги"						                                        , "Сушильные машины"		            ,1.13				, "Сушильная машина"					, "Техника для дома"			    , product);
            matchProduct("03.01.01,03.01.03, Ст/м"										                    , "Стиральные машины"		            ,1.13				, "Стиральная машина"					, "Техника для дома"			    , product);
            matchProduct("03.02.01,Пылесосы"												                , "Пылесосы"				            ,1.18				, "Пылесос"							    , "Техника для дома" 			    , product);
            matchProduct("03.03.01,Утюги"												                    , "Утюги"					            ,1.20				, "Утюг"								, "Техника для дома"			    , product);
            matchProduct("03.07_Швейные машины,Шв. машины"									                , "Швейные машины"			            ,1.18				, "Швейная машина"					    , "Техника для дома"			    , product);
            matchProduct("03.08_Оверлоки,Оверлоки"									                        , "Оверлоки"			                ,1.18				, "Оверлок"					            , "Техника для дома"			    , product);

            matchProduct("03.03.02,03.03.03,03.03.04,Отпариватели,Ручные отпариватели"					    , "Отпариватели"			            ,1.20				, "Отпариватель"						, "Техника для дома"			    , product);

            *//*КУХОННАЯ ТЕХНИКА*//*
            matchProduct("15.01.01.01,Аксессуары к холодильникам"											,"Аксессуары к холодильникам"	        ,2				    , "Аксессуар к холодильнику"			, "Кухонная техника"			    , product);
            matchProduct("15.01.06.01,Аксессуары для мультиварок"											,"Аксессуары для мультиварок"	        ,2				    , "Аксессуар для мультиварки"			, "Кухонная техника"			    , product);
            matchProduct("Аксессуары для мясорубок,15.01.07"											    ,"Аксессуары для мясорубок"	            ,2				    , "Аксессуар для мясорубки"			    , "Кухонная техника"			    , product);
            matchProduct("Аксессуары для соковыжималок"											        ,"Аксессуары для соковыжималок"	        ,2				    , "Аксессуар для соковыжималок"		    , "Кухонная техника"			    , product);
            matchProduct("15.01.02.04,15.23.01.,15.24.03.12,Сопла для перевода на сж. газ"				    ,"Аксессуары для плит"	                ,2				    , "Аксессуар для плит"				    , "Кухонная техника"			    , product);
            matchProduct("15.23.02,15.24.03.11,Таблетки для посудомоечных машин,Аксессуары к встраиваемой технике"	,"Аксессуары для посудомоечных машин",2				, "Аксессуар для посудомоечных машин"	, "Кухонная техника"			    , product);
            matchProduct("15.01.05.01,Кронштейны для СВЧ,15.01.13.03,15.01.13.04,Крышки для СВЧ,Посуда для СВЧ"	,"Кронштейны для СВЧ"	        ,2				    , "Кронштейн для СВЧ"				        , "Кухонная техника"			    , product);
            matchProduct("15.01.13.03,15.01.13.04,Крышки для СВЧ, Посуда для СВЧ"		                    ,"Аксессуары для СВЧ"	                ,2				    , "Аксессуар для СВЧ"				    , "Кухонная техника"			    , product);
            matchProduct("15.01.20.,15.24.03."					                                            ,"Кухонные принадлежности"	            ,2				    , "Кухонная принадлежность"			    , "Кухонная техника"			    , product);
            matchProduct("15.07.03.01"					                                                    ,"Фильтры для вытяжки"	                ,2				    , "Фильтр для вытяжки"				    , "Кухонная техника"			    , product);
            matchProduct("15.07.03.02"					                                                    ,"Аксессуары для вытяжки"	            ,2				    , "Аксессуар для вытяжки"				, "Кухонная техника"			    , product);

            matchProduct("Тостеры"											                                , "Тостеры"	                            ,1.15				, "Тостер"						        , "Кухонная техника"			    , product);
            matchProduct("15.01.27.03"											                            , "Кулеры"	                            ,1.15				, "Кулер"						        , "Кухонная техника"			    , product);
            matchProduct("01.01.02,Холод."											                        , "Холодильники"	                    ,1.15				, "Холодильник"						    , "Кухонная техника"			    , product);
            matchProduct("01.01.03.03,Мороз. верт."											            , "Морозильники"	                    ,1.15				, "Морозильник"						    , "Кухонная техника"			    , product);
            matchProduct("01.01.03.04,Мороз. лари"											                , "Морозильные лари"	                ,1.15				, "Морозильный ларь"					, "Кухонная техника"			    , product);
            matchProduct("01.02.01.01,01.02.01.02,Плита эл."						                        , "Электрические плиты"		            ,1.17				, "Эликтрическая плита"				    , "Кухонная техника"			    , product);
            matchProduct("01.02.03_Плитки электрические,Плитки эл. настольные"						        , "Электрические плитки"	            ,1.17				, "Эликтрическая плитка"				, "Кухонная техника"			    , product);
            matchProduct("01.03.04_Плитки газовые,Плитки газ. настольные"						            , "Газовые плитки"			            ,1.17			    , "Газовая плитка"				        , "Кухонная техника"			    , product);
            matchProduct("01.03.01,01.03.02,Плита газ."						                            , "Газовые плиты"			            ,1.17				, "Газовая плита"						, "Кухонная техника"			    , product);
            matchProduct("01.03.03,Комбинированная плита,Плита газ./эл"						            , "Комбинированные плиты"	            ,1.17				, "Комбинированная плита"				, "Кухонная техника"			    , product);
            matchProduct("01.05,Микр. печи"							                                    , "Микроволновые печи"		            ,1.18				, "Микроволновая печь"				    , "Кухонная техника"			    , product);
            matchProduct("01.18,Чайники эл"							                                    , "Электрические чайники"	            ,1.20				, "Электрический чайник"				, "Кухонная техника"			    , product);
            matchProduct("01.19,Кофеварки, Кофемолки"												        , "Кофеварки"				            ,1.18				, "Кофеварка"							, "Кухонная техника"			    , product);
            matchProduct("01.12,Мультиварки"												                , "Мультиварки"				            ,1.20				, "Мультиварка"						    , "Кухонная техника"			    , product);
            matchProduct("01.08.03,01.08.04,Блендеры"									                    , "Блендеры"				            ,1.20				, "Блендер"							    , "Кухонная техника"			    , product);
            matchProduct("01.08.01,01.08.02,Миксеры"									                    , "Миксеры"					            ,1.20				, "Миксер"							    , "Кухонная техника"			    , product);
            matchProduct("01.10.02,01.10.01,Мясорубки"									                    , "Мясорубки"				            ,1.20				, "Мясорубка"							, "Кухонная техника"			    , product);
            matchProduct("01.15,Хлебопечи"												                    , "Хлебопечи"				            ,1.15				, "Хлебопечь"							, "Кухонная техника"			    , product);
            matchProduct("15.15.02,Кастрюля"											                    , "Кастрюли"				            ,1.35				, "Кастрюля"							, "Кухонная техника"			    , product);
            matchProduct("01.04,Посудомоечная машина"									                    , "Посудомоечные машины"	            ,1.15				, "Посудомоечная машина"				, "Кухонная техника"			    , product);
            matchProduct("07.05.01,07.05.02,07.05.03,07.05.05,07.05.05,Вытяжка каминная,Вытяжка козырьковая,", "Вытяжки"					        ,1.15              , "Вытяжка"				                , "Кухонная техника"			    , product);
            matchProduct("01.09,Соковыжималка"				                                                , "Соковыжималки"			            ,1.18		        , "Соковыжималка"				        , "Кухонная техника"			    , product);
            matchProduct("01.12.01"				                                                        , "Фритюрницы"			                ,1.20		        , "Фритюрница"				            , "Кухонная техника"			    , product);
            matchProduct("01.12.02,Мультиварка"				                                            , "Мультиварки"			                ,1.20		        , "Мультиварка"				            , "Кухонная техника"			    , product);
            matchProduct("01.13,Пароварки"				                                                    , "Пароварки"			                ,1.20		        , "Пароварка"				            , "Кухонная техника"			    , product);
            matchProduct("01.14.01,Блинница,Вафельница"				                                    , "Электропечи"			                ,1.20		        , "Электропечь"				            , "Кухонная техника"			    , product);
            matchProduct("01.14.02,Аэрогриль"				                                                , "Аэрогрили"			                ,1.15		        , "Аэрогриль"				            , "Кухонная техника"			    , product);
            matchProduct("01.16.01,Йогуртница"				                                                , "Йогуртницы"			                ,1.15		        , "Йогуртница"				            , "Кухонная техника"			    , product);
            matchProduct("01.16.02,Электрические грили и барбекю"				                            , "Электрогрили"			            ,1.15		        , "Элетрогриль"				            , "Кухонная техника"			    , product);
            matchProduct("01.16.06,Электрошашлычницы"			                                            , "Шашлычницы"			                ,1.18		        , "Шашлычница"				            , "Кухонная техника"			    , product);
            matchProduct("01.17.02,Вафельницы"				                                                , "Вафельницы"			                ,1.18		        , "Вафельница"				            , "Кухонная техника"			    , product);
            matchProduct("01.17.03"				                                                        , "Мультипекари"			            ,1.18		        , "Мультипекарь"				        , "Кухонная техника"			    , product);
            matchProduct("01.17.04,Орешницы"				                                                , "Орешницы"			                ,1.18		        , "Орешница"				            , "Кухонная техника"			    , product);
            matchProduct("01.25_Блинницы электрические, Блинницы"				                            , "Блинницы"			                ,1.18		        , "Блинница"				            , "Кухонная техника"			    , product);

            *//*ВСТРАИВАЕМАЯ ТЕХНИКА*//*
            matchProduct("07.01.01,Встраиваемый духовой шкаф"			                                    , "Встраиваемые духовые шкафы"			,1.15	            , "Встраиваемый духовой шкаф"			, "Встраиваемая техника"		    , product);
            matchProduct("07.03, 07.04,Встраиваемая варочная поверхность"	                                , "Встраиваемые варочные панели"		,1.15	            , "Встраиваемая варочная панель"		, "Встраиваемая техника"		    , product);
            matchProduct("07.05.04_Вытяжки встраиваемые,Вытяжка встраиваемая"								, "Встраиваемые вытяжки"				,1.15	            , "Встраиваемая вытяжка"				, "Встраиваемая техника"		    , product);
            matchProduct("07.07,Холодильники встраиваемые"								                    , "Встраиваемые холодильники"			,1.15	            , "Встраиваемый холодильник"			, "Встраиваемая техника"		    , product);
            matchProduct("07.08,свч печи встраиваемые"									                    , "Встраиваемые СВЧ"					,1.15	            , "Встраиваемая СВЧ"					, "Встраиваемая техника"		    , product);
            matchProduct("07.06,Посудомоечные машины встраиваемые"						                    , "Встраиваемые посудомоечные машины"	,1.15	            , "Встраиваемая посудомоечная машина"	, "Встраиваемая техника"		    , product);

            *//*ТЕЛЕ-ВИДЕО-АУДИО*//*
            matchProduct("Бытовые удлинители,15.24.01.12"												    , "Удлинители"				            ,2				    , "Удлинитель"						    , "Теле-Видео-Аудио"			    , product);
            matchProduct("Батарейка,15.24.01."												                , "Батарейки"				            ,2				    , "Батарейка"						    , "Теле-Видео-Аудио"			    , product);
            matchProduct("Перезаряжаемые батарейки,15.24.01.,15.24.01."									, "Аккумуляторы"				        ,2				    , "Аккумулятор"						    , "Теле-Видео-Аудио"			    , product);
            matchProduct("Зарядные устройства для перезаряжаемых"									        , "Зарядники батареек"				    ,2				    , "Зарядник батареек"					, "Теле-Видео-Аудио"			    , product);
            matchProduct("10.11.02"												                        , "Магнитолы"				            ,1.13				, "Магнитола"						    , "Теле-Видео-Аудио"			    , product);
            matchProduct("02.02,LED Телевизор"												                , "Телевизоры"				            ,1.13				, "Телевизор"							, "Теле-Видео-Аудио"			    , product);
            matchProduct("15.02.17.01,15.02.18.01,TV-тюнер"	                                            , "Ресиверы для тв"			            ,1.25				, "Цифровой ресивер"					, "Теле-Видео-Аудио"			    , product);
            matchProduct("15.08.24.03,15.02.08,15.02.09,15.02.10, Переходник"		                        , "Кабели ТВ"				            ,1.90				, "ТВ кабель"							, "Теле-Видео-Аудио"			    , product);
            matchProduct("15.02.01,Кронштейны для ТВ"						                                , "Кронштейны ТВ"			            ,2				    , "ТВ кронштейн"						, "Теле-Видео-Аудио"			    , product);
            matchProduct("15.02.17.02,Антенна телевизионная"			                                    , "Антенны ТВ"				            ,1.5				, "ТВ антенна"						    , "Теле-Видео-Аудио"			    , product);
            matchProduct("10.10,Музыкальные центры"										                , "Музыкальные центры"		            ,1.15				, "Музыкальный центр"					, "Теле-Видео-Аудио"			    , product);
            matchProduct("15.02.07,Телемебель"											                    , "Телемебель"				            ,1.40				, "Телемебель"						    , "Теле-Видео-Аудио"			    , product);
            matchProduct("10.17.01,Синтезаторы и цифровые фортепьяно"					                    , "Музыкальные инструменты"	            ,1.15				, "Музыкальный инструмент"			    , "Теле-Видео-Аудио"			    , product);
*/
        }
    }

    private void matchProduct(String alias, String productGroup, double coefficient, String single, String productCategory, OriginalProduct originalProduct) {
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

                /**/

            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

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
