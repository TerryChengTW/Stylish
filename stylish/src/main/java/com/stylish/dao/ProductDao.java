package com.stylish.dao;

import com.stylish.model.Product;
import java.util.List;
import java.util.Map;

public interface ProductDao {
    void insertProduct(Product product);

    List<Map<String, Object>> getProducts(String category, int offset, int limit);
    int getTotalProductCount(String category);
    List<Map<String, Object>> getProductColors(int productId);
    List<String> getProductSizes(int productId);
    List<Map<String, Object>> getProductVariants(int productId);
    List<String> getProductImages(int productId);

    List<Map<String, Object>> searchProducts(String keyword, int offset, int limit);

    int getSearchProductCount(String keyword);

    List<Map<String, Object>> getProductById(int id);
    Product.ProductVariant getProductVariant(int productId, String colorCode, String size);

    void updateProductVariant(Product.ProductVariant variant);
}