package com.stylish.service;

import com.stylish.dao.ProductDao;
import com.stylish.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final int PAGE_SIZE = 6;
    private static final String BASE_URL = "http://18.182.90.188";

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void addProduct(Product product) {
        logger.debug("Adding product: {}", product.getTitle());
        productDao.insertProduct(product);
        logger.debug("Product added with ID: {}", product.getId());
    }

    @Override
    public Map<String, Object> getProductList(String category, int page) {
        int offset = page * PAGE_SIZE;
        List<Map<String, Object>> productsList = productDao.getProducts(category, offset, PAGE_SIZE);
        int totalCount = productDao.getTotalProductCount(category);

        return processProductList(productsList, totalCount, page);
    }

    @Override
    public Map<String, Object> searchProducts(String keyword, int page) {
        int offset = page * PAGE_SIZE;
        List<Map<String, Object>> productsList = productDao.searchProducts(keyword, offset, PAGE_SIZE);
        int totalCount = productDao.getSearchProductCount(keyword);

        return processProductList(productsList, totalCount, page);
    }


    private Map<String, Object> processProductList(List<Map<String, Object>> productsList, int totalCount, int page) {
        List<Map<String, Object>> enrichedProducts = enrichProductsData(productsList);

        Map<String, Object> result = new HashMap<>();
        result.put("data", enrichedProducts);

        int offset = page * PAGE_SIZE;
        if (offset + PAGE_SIZE < totalCount) {
            result.put("next_paging", page + 1);
        }

        return result;
    }
    private List<Map<String, Object>> enrichProductsData(List<Map<String, Object>> productsList) {
        List<Map<String, Object>> enrichedProducts = new ArrayList<>();

        for (Map<String, Object> originalProduct : productsList) {
            int productId = ((Number) originalProduct.get("id")).intValue();

            Map<String, Object> reorganizedProduct = new LinkedHashMap<>();

            reorganizedProduct.put("id", originalProduct.get("id"));
            reorganizedProduct.put("category", originalProduct.get("category"));
            reorganizedProduct.put("title", originalProduct.get("title"));
            reorganizedProduct.put("description", originalProduct.get("description"));
            reorganizedProduct.put("price", originalProduct.get("price"));
            reorganizedProduct.put("texture", originalProduct.get("texture"));
            reorganizedProduct.put("wash", originalProduct.get("wash"));
            reorganizedProduct.put("place", originalProduct.get("place"));
            reorganizedProduct.put("note", originalProduct.get("note"));
            reorganizedProduct.put("story", originalProduct.get("story"));

            List<Map<String, Object>> colors = productDao.getProductColors(productId);
            reorganizedProduct.put("colors", colors);

            List<String> sizes = productDao.getProductSizes(productId);
            reorganizedProduct.put("sizes", sizes);

            List<Map<String, Object>> variants = productDao.getProductVariants(productId);
            reorganizedProduct.put("variants", variants);

            String mainImage = (String) originalProduct.get("main_image");
            reorganizedProduct.put("main_image", BASE_URL + mainImage);

            List<String> images = productDao.getProductImages(productId);
            List<String> enrichedImages = images.stream()
                    .map(image -> BASE_URL + image)
                    .collect(Collectors.toList());
            reorganizedProduct.put("images", enrichedImages);

            enrichedProducts.add(reorganizedProduct);
        }

        return enrichedProducts;
    }


    @Override
    public Map<String, Object> getProductDetails(int id) {
        List<Map<String, Object>> products = productDao.getProductById(id);
        if (products.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> enrichedProducts = enrichProductsData(products);
        return enrichedProducts.get(0);
    }

    @Override
    public List<Map<String, Object>> getAllProductsForCampaign(String keyword) {
        List<Map<String, Object>> allProducts = new ArrayList<>();
        int page = 0;
        while (true) {
            Map<String, Object> result;
            if (keyword != null && !keyword.isEmpty()) {
                result = searchProducts(keyword, page);
            } else {
                result = getProductList("all", page);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("data");
            if (products.isEmpty()) {
                break;
            }
            allProducts.addAll(products);

            if (!result.containsKey("next_paging")) {
                break;
            }
            page++;
        }

        return allProducts.stream()
                .map(this::simplifyProductForCampaign)
                .collect(Collectors.toList());
    }

    private Map<String, Object> simplifyProductForCampaign(Map<String, Object> product) {
        Map<String, Object> simplified = new HashMap<>();
        simplified.put("id", product.get("id"));
        simplified.put("title", product.get("title"));
        simplified.put("story", product.get("story"));
        simplified.put("main_image", product.get("main_image"));
        return simplified;
    }
}