package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.domain.Product;
import server.domain.User;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.services.ProductBuilder;
import server.services.ProductService;
import server.services.UserService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApiController {
    /*!!!ВМЕСТО ОБРАБОТКИ ЗАПРОСВ КАЖДЫЙ РАЗ ЧЕРЕЗ API, СОЗДАНИЕ JSON C ДАННЫМИ ДЛЯ КАЖДОГО СЕРВИСА!!!*/
    private final ProductService productService;
    private final ProductBuilder productBuilder;

    /*Products*/
    @GetMapping("/products/group/{group}/{page}")
    private Page<Product> listProductsByGroupPage(@PathVariable String group, @PathVariable(required = false) int page) {
        return productService.getProductsByGroup(group, PageRequest.of(page, 15, Sort.Direction.ASC, "pic"));
    }

    @GetMapping("/products/show/{productID}")
    private Product listProductByID(@PathVariable String productID) {
        return productService.getProductByID(productID);
    }

    @PostMapping("/products/filter/{group}")
    private Page<Product> filterProducts(@RequestBody Map<String, String[]> filters, @PathVariable String group) {
        return productService.filterProducts(filters, group);
    }

    @GetMapping("/products/page/filters/{group}")
    private FiltersList createFiltersLists(@PathVariable String group) {
        return productService.createProductsFilterLists(group);
    }

    /*@PostMapping("/all/catalog")
    private LinkedHashMap<String, List<ProductGroup>> listFullCatalog(@RequestBody String[] categories) {
        return productService.getAllCategories(categories);
    }*/








    /*Orders*/
    @PostMapping("/order/orderedProducts")
    private List<Product> listOrderedProducts() {
        return productService.listOrderedProducts();
    }

    /*Filters*/




    /*Admin*/
    @PostMapping("/admin/uploadFileDB")
    private void uploadProductsDBFile(@RequestParam("file") MultipartFile file) {
        try {
            productBuilder.updateProductsDB(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/admin/uploadFileBrands")
    private void uploadBrandsPrice(@RequestParam("fileBrands") MultipartFile file) {
        productBuilder.updateBrandsPrice(file);
    }
    @PostMapping("/admin/test")
    private void uploadProductsDBFile(@AuthenticationPrincipal User user) {
        log.info("TEST USER: " + user.getFirstName());
        productBuilder.test();
    }
    @PostMapping("/admin/updateCatalog")
    private void updateProductsCatalog() {
        try
        {
            productBuilder.mapCatalogJSON();
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/admin/parsePicsRUSBT")
    private void parsePicsRUSBT() {
        productBuilder.parsePicsRUSBT();
    }



}
