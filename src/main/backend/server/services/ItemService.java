package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import server.domain.Item;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.repos.ItemRepo;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log
public class ItemService {
    private final ItemRepo itemRepo;

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

        return groups;
    }

    public List<Item> getProductsByGroup(String group) {
        return itemRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group.replaceAll("_"," "));
    }

    public FiltersList createFiltersLists(String group) {
        log.info(group);

        FiltersList filtersList = new FiltersList();
        List<Integer> allPrices = new LinkedList<>();

        /*Спсок товаров по нужной группе с картинками*/
        List<Item> products = itemRepo.findByProductGroupIgnoreCaseAndOriginalPicIsNotNull(group).stream().filter(item ->
                !item.getOriginalAnnotation().isEmpty()).collect(Collectors.toList());

        try {
            /*Все бренды группы*/
            products.forEach(item -> filtersList.brands.add(StringUtils.capitalize(item.getOriginalBrand().toLowerCase())));

            /*Минимальная и максимальная цена в группе*/
            products.forEach(item -> allPrices.add(item.getFinalPrice()));
            allPrices.sort(Comparator.comparingInt(Integer::intValue));
            filtersList.prices.add(allPrices.get(0));
            filtersList.prices.add(allPrices.get(allPrices.size()-1));

            filtersList.showInfo();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return filtersList;
    }
}
