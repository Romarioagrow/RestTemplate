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

@Service
@AllArgsConstructor
@Log
public class ProductService {
    private final ProductRepo itemRepo;

    public List<ProductGroup> getProductGroups(String category) {
        Set<String> productGroups = new HashSet<>();
        List<ProductGroup> groups = new ArrayList<>();

        itemRepo.findByProductCategoryIgnoreCase(category).forEach(item -> productGroups.add(item.getProductGroup()));

        productGroups.forEach(productGroup -> {
            String pic = itemRepo.findFirstByProductGroupAndOriginalPicIsNotNull(productGroup).getOriginalPic();
            ProductGroup group = new ProductGroup();
            group.setGroupName(productGroup);
            group.setGroupPic(pic);
            groups.add(group);
        });
        groups.sort(Comparator.comparing(ProductGroup::getGroupName));
        return groups;
    }

    public List<Product> getProductsByGroup(String group) {
        return itemRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group.replaceAll("_"," "));
    }

    public FiltersList createFiltersLists(String group) {
        log.info(group);

        FiltersList filtersList = new FiltersList();
        List<Integer> allPrices = new LinkedList<>();

        /*Спсок товаров по нужной группе с картинками*/
        List<Product> products = itemRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group).stream().filter(item ->
                !item.getOriginalAnnotation().isEmpty()).collect(Collectors.toList());

        products.forEach(product -> log.info(product.getOriginalAnnotation()));

        try
        {
            /*Все бренды группы*/
            products.forEach(item -> filtersList.brands.add(StringUtils.capitalize(item.getOriginalBrand().toLowerCase())));

            /*Минимальная и максимальная цена в группе*/
            products.forEach(item -> allPrices.add(item.getFinalPrice()));
            allPrices.sort(Comparator.comparingInt(Integer::intValue));

            log.info(allPrices.size() + "");
            filtersList.prices.add(allPrices.get(0));
            filtersList.prices.add(allPrices.get(allPrices.size()-1));

            /*Сформировать фильтры-особенности*/
            products.forEach(product -> {
                String supplier = product.getSupplier();
                String annotation = product.getOriginalAnnotation();

                String splitter = supplier.contains("1RBT") ? "; " : ", ";
                String[] filters = annotation.split(splitter);

                for (String filter : filters)
                {
                    if (filterIsFeature(filter, supplier)) {
                        filtersList.features.add(org.apache.commons.lang3.StringUtils.substringBefore(filter, ":"));
                    }
                }
            });

            System.out.println("\n Features:");
            filtersList.features.forEach(log::info);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return filtersList;
    }

    private boolean filterIsFeature(String filter, String supplier) {
        String[] stopList = {" Л", " СМ", " ЯЩИКА", " ВТ"};
        String[] notEquals = {"A", "A+", "B", "N", "N/ST", "R134A", "R600A", "SN/N/ST", "SN/ST"};

        if ((supplier.contains("RUS-BT") && !filter.contains(":")) || filter.contains(": есть")) {
            for (String word : stopList) {
                if (filter.contains(word)) return false;
            }
            for (String word : notEquals) {
                if (filter.equals(word)) return false;
            }
            return true;
        }
        return false;
    }
}


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