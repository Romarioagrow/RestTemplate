package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import server.domain.Item;
import server.dto.ProductGroup;
import server.repos.ItemRepo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Log
public class ItemService {
    private final ItemRepo itemRepo;

    public List<Item> listAllItems() {
        return itemRepo.findByProductGroupIgnoreCase("музыкальные центры");
    }

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
}
