package com.stylish.controller;

import com.stylish.model.Product;
import com.stylish.model.Product.ProductColor;
import com.stylish.model.Product.ProductVariant;
import com.stylish.service.ProductService;
import com.stylish.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api/1.0/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    private final FileUploadService fileUploadService;

    public ProductController(ProductService productService, FileUploadService fileUploadService) {
        this.productService = productService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@ModelAttribute Product product,
                                        @RequestParam("mainImageFile") MultipartFile mainImageFile,
                                        @RequestParam("otherImageFiles") List<MultipartFile> otherImageFiles,
                                        @RequestParam("colorName[]") List<String> colorNames,
                                        @RequestParam("colorCode[]") List<String> colorCodes,
                                        @RequestParam("size[]") List<String> sizes,
                                        @RequestParam("stock[]") List<Integer> stocks) {
        try {
            // save img to ec2 and set url
            String mainImageUrl = fileUploadService.saveImage(mainImageFile);
            List<String> otherImageUrls = fileUploadService.saveImages(otherImageFiles);

            product.setMainImage(mainImageUrl);
            product.setOtherImages(otherImageUrls);
            Set<ProductColor> colors = new LinkedHashSet<>();
            for (int i = 0; i < colorNames.size(); i++) {
                ProductColor color = new ProductColor();
                color.setName(colorNames.get(i));
                color.setCode(colorCodes.get(i));
                colors.add(color);
            }
            product.setColors(new ArrayList<>(colors));

            Set<Product.ProductSize> productSizes = new LinkedHashSet<>();
            for (String size : sizes) {
                Product.ProductSize productSize = new Product.ProductSize();
                productSize.setSize(size);
                productSizes.add(productSize);
            }
            product.setSizes(new ArrayList<>(productSizes));


            List<ProductVariant> variants = new ArrayList<>();
            for (int i = 0; i < sizes.size(); i++) {
                ProductVariant variant = new ProductVariant();
                variant.setColorCode(colorCodes.get(i % colorCodes.size()));
                variant.setSize(sizes.get(i));
                variant.setStock(stocks.get(i));
                variants.add(variant);
            }
            product.setVariants(variants);

            productService.addProduct(product);
            logger.debug("Added product: {}", product);

            Map<String, Object> response = new HashMap<>();
            response.put("id", product.getId());

            return ResponseEntity.ok(response);

        } catch (IOException | SQLException e) {
            logger.error("Error adding product", e);
            return ResponseEntity.badRequest().body("Error adding product: " + e.getMessage());
        }
    }


    private static final Set<String> VALID_CATEGORIES = new HashSet<>(Arrays.asList("all", "men", "women", "accessories"));

    @GetMapping({"/{category}"})
    public ResponseEntity<Map<String, Object>> getProducts(
            @PathVariable(required = false) String category,
            @RequestParam(defaultValue = "0") int paging) {

        if (!VALID_CATEGORIES.contains(category)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid category. Valid categories are: all, men, women, accessories"));
        }

        if (paging < 0) {
            logger.warn("Invalid paging parameter: {}", paging);
            return ResponseEntity.badRequest().body(Map.of("error", "Paging parameter can't be a negative integer"));
        }

        Map<String, Object> response = productService.getProductList(category, paging);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int paging) {

        logger.info("Searching products with keyword: {} and paging: {}", keyword, paging);

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Keyword cannot be empty"));
        }

        if (paging < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Paging cannot be negative"));
        }

        try {
            Map<String, Object> result = productService.searchProducts(keyword, paging);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error occurred while searching products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while processing your request"));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getProductDetails(@RequestParam int id) {
        logger.info("Fetching product details for id: {}", id);

        if (id < 1) {
            return ResponseEntity.badRequest().body(Map.of("error", "The minimum id is 1"));
        }

        try {
            Map<String, Object> productDetails = productService.getProductDetails(id);
            if (productDetails == null) {
                logger.warn("Product not found for id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Product not found"));
            }
            return ResponseEntity.ok(Map.of("data", productDetails));
        } catch (Exception e) {
            logger.error("Error occurred while fetching product details for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while processing your request"));
        }
    }

    @GetMapping("/all-for-campaign")
    public ResponseEntity<?> getAllProductsForCampaign(@RequestParam(required = false) String keyword) {
        List<Map<String, Object>> products = productService.getAllProductsForCampaign(keyword);
        return ResponseEntity.ok(Map.of("data", products));
    }
}