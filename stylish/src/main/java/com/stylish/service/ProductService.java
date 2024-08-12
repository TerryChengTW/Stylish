package com.stylish.service;

import com.stylish.model.Product;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    void addProduct(Product product) throws SQLException;
    Map<String, Object> getProductList(String category, int page);

    Map<String, Object> searchProducts(String keyword, int page);

    Map<String, Object> getProductDetails(int id);

    List<Map<String, Object>> getAllProductsForCampaign(String keyword);
}
