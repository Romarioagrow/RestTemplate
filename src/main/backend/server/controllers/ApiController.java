package server.controllers;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.domain.OriginalProduct;
import server.dto.FiltersList;
import server.dto.ProductGroup;
import server.services.ProductBuilder;
import server.services.ProductService;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiController {
    private final ProductService productService;
    private final ProductBuilder productBuilder;

    /*Products*/
    @GetMapping("/products/{group}")
    private List<OriginalProduct> listProductsByGroup(@PathVariable String group) {
        /// return productService.getProductsByGroup(group);
        return new ArrayList<>();
    }
    @GetMapping("/products/product/{productID}")
    private OriginalProduct listProductByID(@PathVariable String productID) {
        return productService.getProductByID(productID);
    }

    /*Catalog*/
    @GetMapping("/catalog/{category}")
    private List<ProductGroup> listProductGroupsOfCategory(@PathVariable String category) {
        return productService.getProductGroups(category);
    }

    /*Filters*/
    @GetMapping("/filters/construct/{group}")
    private FiltersList createFiltersLists(@PathVariable String group) {
        return productService.createProductsFilterLists(group);
    }

    /*Admin*/
    @PostMapping("/admin/uploadFileDB")
    private void uploadProductsDBFile(@RequestParam("file") MultipartFile file) throws UnsupportedEncodingException {
        try
        {
            productBuilder.updateProductsDB(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
