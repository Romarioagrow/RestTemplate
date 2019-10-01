package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import server.domain.Product;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.repos.ProductRepo;
import java.util.*;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.*;

@Service
@AllArgsConstructor
@Log
public class ProductService {
    private final ProductRepo productRepo;

    public List<Product> getProductsByGroup(String group) {
        return productRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group.replaceAll("_"," "));
    }

    public List<ProductGroup> getProductGroups(String category) {
        Set<String> productGroups = new HashSet<>();
        List<ProductGroup> groups = new ArrayList<>();

        productRepo.findByProductCategoryIgnoreCase(category).forEach(item -> productGroups.add(item.getProductGroup()));

        productGroups.forEach(productGroup -> {
            String pic = productRepo.findFirstByProductGroupAndOriginalPicIsNotNull(productGroup).getOriginalPic();
            ProductGroup group = new ProductGroup();
            group.setGroupName(productGroup);
            group.setGroupPic(pic);
            groups.add(group);
        });
        groups.sort(Comparator.comparing(ProductGroup::getGroupName));
        return groups;
    }

    public FiltersList createFiltersLists(String group) {
        log.info(group);

        FiltersList filtersList = new FiltersList();
        List<Integer> allPrices = new LinkedList<>();

        /*Наполнение списка товаров нужной группы*/
        List<Product> products = productRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group).stream().filter(item ->
                !item.getOriginalAnnotation().isEmpty()).collect(Collectors.toList());

        products.forEach(product -> log.info(product.getOriginalAnnotation())); ///

        try
        {
            /*Сформировать фильтры-бренды группы*/
            {
                products.forEach(item -> filtersList.brands.add(StringUtils.capitalize(item.getOriginalBrand().toLowerCase())));
            }

            /*Сформировать фильры цены*/
            {
                products.forEach(item -> allPrices.add(item.getFinalPrice()));
                allPrices.sort(Comparator.comparingInt(Integer::intValue));

                /*Минимальная и максимальная цена в группе*/
                log.info(allPrices.size() + "");
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
                        if (filterIsFeature(filter, supplier)) {
                            filtersList.features.add(substringBefore(filter, ":").toUpperCase());
                        }

                        /*Сформировать фильтры-параметры*/
                        else if (filterIsParam(filter)) {
                            String key = substringBefore(filter, ":");
                            String val = substringAfter(filter, ": ");

                            if (filtersList.paramFilters.get(key) != null)
                            {
                                TreeSet<String> vals = filtersList.paramFilters.get(key);
                                vals.add(val);
                                filtersList.paramFilters.put(key, vals);
                            }
                            else filtersList.paramFilters.putIfAbsent(key, new TreeSet<>(Collections.singleton(val)));
                        }
                    }
                });

                System.out.println("\n До просева:"); ///
                filtersList.features.forEach(log::info); ///

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

                ////
                System.out.println("\n Features:"); ///
                filtersList.features.forEach(log::info); ///
                System.out.println("\n filters params:"); ///
                filtersList.paramFilters.forEach((s, strings) -> {
                    log.info(s + " " + strings);
                }); ///
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return filtersList;
    }

    private boolean filterIsParam(String filter) {
        String[] notParam = {"нет", "0", "количество шт в", "-"};
        if (filter.contains(":") && !filter.contains("количество шт в")) {
            for (String word : notParam) {
                String checkParam = substringAfter(filter, ":").trim();
                if (checkParam.startsWith(word)) return false;
            }
            return true;
        }
        return false;

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