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

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderApiController {
    private final OrderService orderService;

    @PostMapping("/orderedProducts")
    private List<Product> listOrderedProducts() {
        return orderService.listOrderedProducts();
    }

    @PostMapping("/addProduct")
    private boolean addProductToOrder(@RequestBody String productID, @AuthenticationPrincipal User user) {
        return orderService.addProductToOrder(productID, user);
    }

}
