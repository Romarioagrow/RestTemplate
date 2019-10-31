package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import server.domain.User;
import server.services.OrderService;
import java.util.LinkedList;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderApiController {
    private final OrderService orderService;

    @PostMapping("/addProduct")
    private boolean addProductToOrder(@RequestBody String productID, @AuthenticationPrincipal User user) {
        return orderService.addProductToOrder(productID, user);
    }

    @PostMapping("/orderedProducts")
    private LinkedList<Object> getOrderedProducts(@AuthenticationPrincipal User user) {
        return orderService.getOrderData(user);
    }

    @PostMapping("/deleteProduct")
    private LinkedList<Object> deleteProductFromOrder(@RequestBody String productID, @AuthenticationPrincipal User user) {
        return orderService.deleteProductFromOrder(productID, user);
    }
}
