package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.domain.Item;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.services.ItemService;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Log
public class ApiController {
    private final ItemService itemService;

    /*@GetMapping
    private List<Item> restApi() {
        return itemService.listAllItems();
    }*/

    @GetMapping("/catalog/{category}")
    private List<ProductGroup> getGroupsInCategory(@PathVariable String category) {
        return itemService.getProductGroups(category);
    }

    @GetMapping("/products/{group}")
    private List<Item> listProductsByGroup(@PathVariable String group) {
        return itemService.getProductsByGroup(group);
    }

    @GetMapping("/filters/construct/{group}")
    private FiltersList createFiltersLists(@PathVariable String group) {
        return itemService.createFiltersLists(group);
    }
}
