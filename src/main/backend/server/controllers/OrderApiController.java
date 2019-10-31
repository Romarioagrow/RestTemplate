package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.domain.Product;
import server.domain.User;
import server.services.OrderService;
import java.util.List;
import java.util.Map;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderApiController {
    private final OrderService orderService;

    @PostMapping("/orderedProducts")
    private Map<String, Integer> listOrderedProducts(@AuthenticationPrincipal User user) {
        return orderService.listOrderedProducts(user);
    }

    @PostMapping("/addProduct")
    private boolean addProductToOrder(@RequestBody String productID, @AuthenticationPrincipal User user) {
        return orderService.addProductToOrder(productID, user);
    }

}
