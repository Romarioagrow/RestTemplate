package server.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.domain.Product;
import server.services.ProductService;

import java.util.List;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderApiController {
    @PostMapping("/order/orderedProducts")
    private List<Product> listOrderedProducts() {
        //return productService.listOrderedProducts();
        return null;
    }
}
