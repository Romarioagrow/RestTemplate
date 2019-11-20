package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import server.domain.Product;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.repos.ProductRepo;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.*;

@Log
@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;

    public Page<Product> getProductsByGroup(String group, Pageable pageable) {
        return productRepo.findByProductGroupIgnoreCase(group, pageable);
    }

    /*Создать фильтры для группы products*/
    public FiltersList createProductsFilterLists(String group) {
        FiltersList filtersList = new FiltersList();

        /*Наполнение списка товаров нужной группы*/

        /* List<Product> products = productRepo.findProductsByProductGroupIgnoreCaseAndSupplier(group, "RBT");*/
        List<Product> products = productRepo.findProductsByProductGroupIgnoreCase(group);

        try
        {
            /*Сформировать фильтры-бренды группы*/
            products.forEach(product -> filtersList.brands.add(StringUtils.capitalize(product.getBrand().toLowerCase())));

            /*Сформировать фильтры-цены*/
            List<Integer> allPrices = new LinkedList<>();
            products.forEach(item -> allPrices.add(item.getFinalPrice()));
            allPrices.sort(Comparator.comparingInt(Integer::intValue));
            filtersList.prices.add(allPrices.get(0));
            filtersList.prices.add(allPrices.get(allPrices.size()-1));

            /*Сформировать обрабатываемые фильтры*/
            products.forEach(product ->
            {
                String supplier   = product.getSupplier();
                String annotation = product.getAnnotation();

                /*Разбиение аннотации экземпляра Product на фильтры*/
                String splitter  = supplier.contains("RBT") ? "; " : ", ";
                String[] filters = annotation.split(splitter);

                /*Итерация и отсев неподходящих под фильтры-особенности*/
                for (String filter : filters)
                {
                    /*Сформировать фильтры-особенности*/
                    if (filterIsFeature(filter, supplier)) {
                        /*Наполнение фильтров-особенностей*/
                        filtersList.features.add(substringBefore(filter, ":").toUpperCase());

                        /*Отсев дублей фильтров и синонимов фильтров*/ ///
                        ///filtersList.features.removeIf(featureFilter -> Arrays.stream(notParams).parallel().anyMatch(filterIsDuplicate(checkDuplicate, featureFilter)));
                        List<String> remove = new ArrayList<>();
                        filtersList.features.forEach(featureFilter -> {
                            for (String checkDuplicate : filtersList.features) {
                                if (filterIsDuplicate(checkDuplicate, featureFilter)) {
                                    remove.add(checkDuplicate);
                                }
                            }
                        });
                        filtersList.features.removeAll(remove);
                    }
                    else if (filterIsParam(filter)) {
                        String key = substringBefore(filter, ":");
                        String val = substringAfter(filter, ": ");

                        /*Digit diapasons*/
                        if (filterIsDiapasonParam(val)) {
                            try {
                                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                                Number number = format.parse(val);
                                Double parsedValue = number.doubleValue();

                                if (filtersList.diapasonsFilters.get(key) != null) {
                                    TreeSet<Double> vals = filtersList.diapasonsFilters.get(key);
                                    vals.add(parsedValue);
                                    filtersList.diapasonsFilters.put(key, vals);
                                }
                                else filtersList.diapasonsFilters.putIfAbsent(key, new TreeSet<>(Collections.singleton(parsedValue)));
                            }
                            catch (ParseException e) {
                                e.getSuppressed();
                            }
                        }

                        /*Сформировать фильтры-параметры*/
                        else {
                            if (filtersList.paramFilters.get(key) != null) {
                                TreeSet<String> vals = filtersList.paramFilters.get(key);
                                vals.add(val);
                                filtersList.paramFilters.put(key, vals);
                            }
                            else filtersList.paramFilters.putIfAbsent(key, new TreeSet<>(Collections.singleton(val)));
                        }
                    }
                }
            });

            /// distinctDiapason()
            filtersList.diapasonsFilters.forEach((key, val) -> {
                Double first = Math.floor(val.first());
                Double last = val.last();
                val.clear();
                val.add(first);
                val.add(last);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return filtersList;
    }

    private boolean filterIsFeature(String filter, String supplier) {
        String[] notContains = {"X1080","''"," ГБ", "Х720", "X480", "МА*Ч"," ГЦ"," ПИНЦЕТОВ", "220 В", " КВТ","АВТООТКЛЮЧЕНИЕ","НЕРЖ."," БАР", " °C","КЭН","ПЭН", "ТЭН", " ГР","АЛЮМИНИЙ /", "T +6 - 20C", "АРТ.","ТРС-3", " Л", " СМ", " ВТ", " ОБ/МИН", " КГ", "в формате HDTV", "КУБ.М/ЧАС", " ДУХОВКА", "КРЫШКА"};
        String[] notEquals   = {"TN", "TFT","A", "A+", "B", "N", "N/ST", "R134A", "R600A", "SN/N/ST", "SN/ST", "ST"};
        String[] colors      = {"МРАМОР", "КОРИЧНЕВЫЙ", "БОРДОВЫЙ", "ВИШНЕВЫЙ", "ЗЕЛЕНЫЙ", "ЗОЛОТОЙ", "КРАСНЫЙ", "РОЗОВЫЙ", "САЛАТОВЫЙ", "БОРДО", "МРАМОР", "СЕРЕБРИСТЫЙ", "СЕРЫЙ", "СИНИЙ", "ФИСТАШКОВЫЙ", "ЦВЕТНОЙ"};
        String[] synonyms    = {"ВЛАГОЗАЩИЩЕННЫЙ КОРПУС", "СЛОТ ДЛЯ ПАМЯТИ", "САМООЧИСТКА", "ВЕРТ. ОТПАРИВАНИЕ", "АВТООТКЛЮЧЕНИЕ", "СИСТЕМА РЕВЕРСА", "3D-НАГРЕВ"};

        /*Основное условие*/
        if ((filter.contains(": есть") || filter.contains(": да")) || (supplier.contains("RUS-BT") && !filter.contains(":")))
        {
            /*Спецефический отсев*/ /// В мультипоток
            for (String word : notContains) {
                if (containsIgnoreCase(filter, word)) return false;
            }
            for (String word : notEquals) {
                if (equalsIgnoreCase(filter, word)) return false;
            }
            for (String word : colors) {
                if (containsIgnoreCase(filter, word)) return false;
            }
            for (String word : synonyms) {
                if (containsIgnoreCase(filter, word)) return false;
            }
            return true;
        }
        return false;
    }

    private boolean filterIsParam(String filter) {
        String[] notParams = {"нет", "0",  "-"};
        String[] notKeys = {"количество шт в"};
        String[] duplicates = {"БРИТВЕННЫХ ГОЛОВОК"};

        /*Основное условие*/
        if (filter.contains(":"))
        {
            for (String word : notKeys) {
                if (filter.startsWith(word)) return false;
            }
            for (String word : notParams) {
                String checkParam = substringAfter(filter, ":").trim();
                if (checkParam.startsWith(word)) return false;
            }
            for (String word : duplicates) {
                if (filter.startsWith(word)) return false;
            }
            return true;
        }
        return false;
    }

    private boolean filterIsDiapasonParam(String val) {
        Pattern pattern = Pattern.compile("^[0-9]{1,10}([,.][0-9]{1,10})?$");
        Matcher matcher = pattern.matcher(val);
        return !val.equals("0") && matcher.matches();
    }

    private boolean filterIsDuplicate(String checkDuplicate, String featureFilter) {
        String[] dontMatch = {"FULL HD (1080P)", "ULTRA HD (2160P)"};

        if (containsIgnoreCase(checkDuplicate, featureFilter) & !equalsIgnoreCase(checkDuplicate, featureFilter))
        {
            for (String word : dontMatch) {
                if (word.contains(checkDuplicate)) return false;
            }
            return true;
        }
        return false;
    }

    public Product getProductByID(String productID) {
        return productRepo.findByProductID(productID);
    }

    /*filter by pic and then by price*/

    public Page<Product> filterProducts(Map<String, String[]> filters, String group, Pageable pageable) {
        List<Product> products = productRepo.findByProductGroupIgnoreCase(group);
        try
        {
            /*Фильтры по цене*/
            products = products.stream().filter(product ->
            {
                String[] priceFilters = filters.get("prices");
                int minPrice = Integer.parseInt(priceFilters[0].trim());
                int maxPrice = Integer.parseInt(priceFilters[1].trim());
                return product.getFinalPrice() >= minPrice && product.getFinalPrice() <= maxPrice;
            }).collect(Collectors.toList());

            /*Фильтры по брендам*/
            if (filterHasContent(filters, "brands")) {
                products = products.stream().filter(product ->
                {
                    String brandFilters = Arrays.toString(filters.get("brands"));
                    return containsIgnoreCase(brandFilters, product.getBrand().trim());
                }).collect(Collectors.toList());
            }

            /*Фильтры по диапазонам*/
            if (filterHasContent(filters, "selectedDiapasons")) {
                products = products.stream().filter(product ->
                {
                    String[] selectedDiapasons = filters.get("selectedDiapasons");
                    String annotation = product.getShortAnnotation();

                    for (String diapason : selectedDiapasons)
                    {
                        String diapasonKey  = substringBefore(diapason, ":");
                        Double minimum = Double.parseDouble(substringBetween(diapason, ":",","));
                        Double maximum = Double.parseDouble(substringAfter(diapason, ","));

                        if (minimum == null || maximum == null) return false;

                        if (annotation.contains(diapasonKey)) {
                            String parseValue = substringBetween(annotation, diapasonKey, ";");
                            if (parseValue.contains(": ")) parseValue = substringAfter(parseValue, ": ");

                            Double checkVal = Double.parseDouble(parseValue.replaceAll(",","."));
                            if (checkVal == null || checkVal < minimum || checkVal > maximum) return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
            }

            /*Фильтры по параметрам*/
            if (filterHasContent(filters, "params")) {
                products = products.stream().filter(product ->
                {
                    String annotation = product.getShortAnnotation();
                    for (String param : filters.get("params")) {
                        if (annotation.contains(param)) return true;
                    }
                    return false;
                }).collect(Collectors.toList());
            }

            /*Фильтры по особенностям*/
            if (filterHasContent(filters, "features")) {
                products = products.stream().filter(product ->
                {
                    String annotation = product.getShortAnnotation();
                    for (String feature : filters.get("features")) {
                        if (containsIgnoreCase(annotation, feature)) return true;
                    }
                    return false;
                }).collect(Collectors.toList());
            }
        }
        catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
        products.sort(Comparator.comparing(Product::getSupplier));
        return productsPage(products, pageable);
    }

    private boolean filterHasContent(Map<String, String[]> filters, String selectedDiapasons) {
        return !Arrays.toString(filters.get(selectedDiapasons)).equals("[]");
    }

    private Page<Product> productsPage(List<Product> products, Pageable pageable) {
        //Pageable pageable = PageRequest.of(0, 100, Sort.Direction.DESC, "supplier"); /// добавить пагинацию при фильтрации
        try{
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), products.size());
            return new PageImpl<>(products.subList(start, end), pageable, products.size());
        }
        catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
        return null;
    }

    public List<Product> searchProducts(String searchRequest) {
        log.info(searchRequest);

        List<Product> products;

        /*Поиск по shortSearchName*/
        String search = searchRequest.replaceAll(" ", "").replaceAll("-", "").toLowerCase();

        products = productRepo.findAll().stream().filter(product ->
                containsIgnoreCase(product.getSearchName(), search)).collect(Collectors.toList());


        /*if (products.size() != 0) */
        log.info(products.size()+"");

        return products;

        /*Поиск по вхождению в оригинальное название*/
        /*products = productRepo.findByOriginalNameContainsIgnoreCaseAndIsAvailableTrue(searchRequest);
        if (products.size() != 0) return products;*/

        /*поиск по раздельным словам массив*/
        /// ()

        /*Поиск по группам*/
        /*products = productRepo.findByOriginalTypeContainsIgnoreCaseAndIsAvailableTrueAndFinalPriceIsNotNull(searchRequest);
        if (products.size() != 0) return products;
        return products;



        return null;*/
    }
}

/*КАК ВАРИАНТ СОЗДАТЬ ШАБЛОНЫ ДЛЯ ДОБАЛВЕНИЯ KEY/VALUE ДЛЯ RUSBT SHORTANNO*/
/*ДОБАВЛЯТЬ СИНОНИМЫ ЧЕРЕЗ ИЛИ*/
/*НУЖНО ЧТО БЫ ПРОДОЛЖАЛ ФИЛЬТРОВАТЬ УЖЕ ОТФИЛЬТРОВАННЫЕ ОДИН РАЗ ТОВАРЫ*/
/*ДЛЯ КАЖДОЙ ФИЛЬТРАЦИИ ОТПРАВЛЯТЬ СПИСОК УЖЕ ОТФИЛЬТРОВАННЫХ ТОВАРОВ*/
/*НА CHECK IN ФИЛЬТРОВАТЬ CURRENT_PRODUCT_FILTER, НА CHECK OUT PRODUCTS = BY GROUP */

/*ФИЛЬТРОВАТЬ УЖЕ НАПОЛНЕНУЮ КОЛЛЕКЦИЮ PRODUCTS И ПЕРЕРИСОВЫВАТЬ ДОСТУПНЫЕ ФИЛЬТРЫ ИЗ НЕЕ*/

/*!!FORMATTED ANNOTATION: УДАЛИТЬ ИЗ АННОТАЦИИ ВСЕ "НЕТ", "-" И ТД, B ВСТАВИТЬ ПЕРЕНОС СТРОКИ ДЛЯ КАЖДОГО*/

/*
    * ЗАГРУЗКА ДАННЫХ НА СТРАНИЦЕ PRODUCTS
    * beforeCreated многопоточно:
    * List/Page product,
    * List<Filters>
      после завершения всех операций loading = false
    * */

/*
 * ПРИ ФИЛЬТРАЦИИ ВЫВОДИТЬ TOTAL PAGES ТАК ЖЕ КАК ПРИ ЗАГРУЗКЕ ВСЕХ PRODUCTS*/

/*
 * ДЛЯ ФИЛЬТРАЦИИ RUSBT ДОБАВЛЯТЬ ЕДИНИЦУ ИЗМЕРЕНИЯ ИЗ АННОТАЦИИ*/

/*!!!!!
 * Для работы с поставщиками использовать сущность OriginalProduct, для работы с сервисами и выводом Product
 * Для работы с самим заказом вцелом Order, внутри которого находится OrderList, products добавлять в OrderList*/
/*!!!!

 */
/*Все внутренние методы для фильтрации в FilterService*/

/*При фильтрации обрабатывать итерируемый фильтр, для нужных случаев по группам товаров добавлять синоним для фильтра*/

/*Добавить возможность обязательного добавления собственного фильтра в фильтры*/

/*
 * В базе у products специальное поле для фильтрации аннтоации, filteredAnnotation, все пробелы в словах внутри фильтра заменить на _, и при фильтрации у фильтра аналогично*/

/*при выборе фильтра-особенности, отображать этот фильтр на карточке товара!*/

/*
 * Сначала ишет уникальные фильтры через И, если их ноль, то тогда через или*/

/*
 * если у двух поставщиков есть одинаковое свойство с одним и тем же словом в названии,
 * в списке оставлять свойство с наименьшим количеством слов в названии: из DVB-T2 и Приём DVB-T2 оставить DVB-T2,
 * фильтровать по contains */

/*
 * При фильтрации в filterService фильтров-особенностей
 * искать по contains(filter) || contains(filter.concat(": есть"))
 * для некоторых фильтров добавить метод с присвоеннием синонимов (Ultra hd = 4K) и проверять на contains синонима
 * так же сформировать списки-фильтры: цвет, габариты, объем, тип, мощность (Ширина: 60, Глубина: 70, высота: 140 = 60х70х140 как минимальный вариант )
 *
 */

/*
 * Формировать shortAnnotation по несколько пунктов из аннотации для каждой группы*/
