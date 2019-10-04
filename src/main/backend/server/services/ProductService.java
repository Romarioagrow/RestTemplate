package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
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

    public List<Product> getProductsByGroup(String group) {
        return productRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group.replaceAll("_"," "));
    }

    public List<ProductGroup> getProductGroups(String category) {
        Set<String> groups = new HashSet<>();
        List<ProductGroup> productGroups = new ArrayList<>();

        /*!!! ПЕРЕПИСАТЬ КОГДА БУДЕТ НОВЫЙ ProductParser С НОВЫМИ ПОЛЯМИ PRODUCTS !!!*/
        /*Находятся все products в категории и просеиваются группы*/
        productRepo.findByProductCategoryIgnoreCase(category).forEach(product -> groups.add(product.getProductGroup())); ////

        log.info(groups.isEmpty() + "");
        if (!groups.isEmpty())
        {
            groups.forEach(productGroup -> {
                String pic = productRepo.findFirstByProductGroupAndOriginalPicIsNotNull(productGroup).getOriginalPic();
                ProductGroup group = new ProductGroup();
                group.setGroupName(productGroup);
                group.setGroupPic(pic);
                productGroups.add(group);
            });
        }
        else
        {
            try {
                log.info(category);
                productRepo.findByOriginalCategoryContainsIgnoreCase(category).forEach(product -> groups.add(product.getOriginalType())); ////
                groups.forEach(originalType -> {
                    String pic = productRepo.findFirstByOriginalTypeAndOriginalPicIsNotNull(originalType).getOriginalPic();
                    if (pic == null) pic = "/pics/toster.png";

                    ProductGroup group = new ProductGroup();
                    group.setGroupName(originalType);
                    group.setGroupPic(pic);
                    productGroups.add(group);
                });
            }
            catch (NullPointerException e) {
                e.getSuppressed();
            }
        }


        productGroups.sort(Comparator.comparing(ProductGroup::getGroupName));

        System.out.println();
        productGroups.forEach(productGroup -> log.info(productGroup.getGroupName()));
        return productGroups;
    }

    public FiltersList createProductsFilterLists(String group) {
        FiltersList filtersList = new FiltersList();
        List<Integer> allPrices = new LinkedList<>();

        /*Наполнение списка товаров нужной группы*/
        List<Product> products = productRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group).stream().filter(item ->
                !item.getOriginalAnnotation().isEmpty()).collect(Collectors.toList());
        /*!!! ОТРЕДАКИТРОВАТЬ КОГДА БУДЕТ ГОТОВ ProductParser !!!*/
        if (products.isEmpty()) products = productRepo.findByOriginalTypeIgnoreCase(group).stream().filter(item ->
                !item.getOriginalAnnotation().isEmpty()).collect(Collectors.toList());

        try
        {
            /*Сформировать фильтры-бренды группы*/
            {
                products.forEach(item -> filtersList.brands.add(StringUtils.capitalize(item.getOriginalBrand().toLowerCase())));
            }

            /*Сформировать фильтры-цены*/
            {
                products.forEach(item -> allPrices.add(item.getFinalPrice()));
                allPrices.sort(Comparator.comparingInt(Integer::intValue));

                /*Минимальная и максимальная цена в группе*/
                filtersList.prices.add(allPrices.get(0));
                filtersList.prices.add(allPrices.get(allPrices.size()-1));
            }

            /*Сформировать фильтры*/
            {
                products.forEach(product -> {
                    String supplier   = product.getSupplier();
                    String annotation = product.getOriginalAnnotation();

                    /*Разбиение аннотации экземпляра Product на фильтры*/
                    String splitter  = supplier.contains("1RBT") ? "; " : ", ";
                    String[] filters = annotation.split(splitter);

                    /*Итерация и отсев неподходящих под фильтры-особенности*/
                    for (String filter : filters)
                    {
                        /*Сформировать фильтры-особенности*/
                        if (filterIsFeature(filter, supplier))
                        {
                            /// method()
                            filtersList.features.add(substringBefore(filter, ":").toUpperCase());

                            /*Отсев дублей фильтров и синонимов фильтров*/
                            List<String> remove = new ArrayList<>();
                            filtersList.features.forEach(featureFilter ->
                            {
                                for (String checkDuplicate : filtersList.features)
                                {
                                    if (filterIsDuplicate(checkDuplicate, featureFilter)) {
                                        remove.add(checkDuplicate);
                                    }
                                }
                            });
                            filtersList.features.removeAll(remove);
                        }
                        else if (filterIsParam(filter))
                        {
                            String key = substringBefore(filter, ":");
                            String val = substringAfter(filter, ": ");

                            /*Digit diapasons*/
                            if (filterIsDiapasonParam(val))
                            {
                                /// method()
                                try
                                {
                                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                                    Number number = format.parse(val);
                                    Double parsedValue = number.doubleValue();

                                    if (filtersList.diapasonsFilters.get(key) != null)
                                    {
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
                            else
                            {
                                /// method()
                                if (filtersList.paramFilters.get(key) != null)
                                {
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

                //filtersList.showDiapasons();
                filtersList.showInfo();
            }
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
        if ((filter.contains(": есть") || filter.contains(": да")) || (supplier.contains("RUS-BT") && !filter.contains(":"))) /// filter.contains(": да")
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
        };
        return false;
    }

    public Product getProductByID(String productID) {
        return productRepo.findProductByProductID(productID);
    }
}

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
Алгоритм наполнения фильтров
1. наполнение списка товаров нужной группы

* */

/*
 * если у двух поставщиков есть одинаковое свойство с одним и тем же словом в названии,
 * в списке оставлять свойство с наименьшим количеством слов в названии: из DVB-T2 и Приём DVB-T2 оставить DVB-T2,
 * фильтровать по contains */

/// Заметки
/*
 * При фильтрации в filterService фильтров-особенностей
 * искать по contains(filter) || contains(filter.concat(": есть"))
 * для некоторых фильтров добавить метод с присвоеннием синонимов (Ultra hd = 4K) и проверять на contains синонима
 * так же сформировать списки-фильтры: цвет, габариты, объем, тип, мощность (Ширина: 60, Глубина: 70, высота: 140 = 60х70х140 как минимальный вариант )
 *
 */

/*
 * Формировать shortAnnotation по несколько пунктов из аннотации для каждой группы*/