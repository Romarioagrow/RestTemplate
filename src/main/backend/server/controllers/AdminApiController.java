package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.domain.Order;
import server.domain.User;
import server.services.OrderService;
import server.services.ProductBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminApiController {
    private final ProductBuilder productBuilder;
    private final OrderService orderService;

    @GetMapping("/acceptedOrders")
    private List<Order> getAcceptedOrders() {
        return orderService.getAcceptedOrders();
    }

    @GetMapping
    private boolean isAdmin(@AuthenticationPrincipal User user) {
        return user.isAdmin();
    }

    @PostMapping("/uploadFileDB")
    private void uploadProductsDBFile(@RequestParam("file") MultipartFile file) {
        try {
            productBuilder.updateProductsDB(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/updateCatalog")
    private void updateProductsCatalog() {
        try {
            productBuilder.mapCatalogJSON();
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/uploadFileBrands")
    private void uploadBrandsPrice(@RequestParam("fileBrands") MultipartFile file) {
        productBuilder.updateBrandsPrice(file);
    }

    @PostMapping("/parsePicsRUSBT")
    private void parsePicsRUSBT() {
        productBuilder.parsePicsRUSBT();
    }

    @PostMapping("/test")
    private void uploadProductsDBFile(@AuthenticationPrincipal User user) {
        log.info("TEST USER: " + user.getFirstName());
        productBuilder.test();
    }
}
