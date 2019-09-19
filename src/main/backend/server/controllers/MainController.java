package server.controllers;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.domain.Item;
import server.services.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class MainController {
    private final ItemService itemService;

    @GetMapping
    private List<Item> restApi() {
        return itemService.listAllItems();
    }
}
