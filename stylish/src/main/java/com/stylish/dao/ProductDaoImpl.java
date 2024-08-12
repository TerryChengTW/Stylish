package com.stylish.dao;

import com.stylish.model.Product;
import com.stylish.model.Product.ProductColor;
import com.stylish.model.Product.ProductVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class ProductDaoImpl implements ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(ProductDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public ProductDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertProduct(Product product) {
        String sql = "INSERT INTO products (category, title, description, price, texture, wash, place, note, story, main_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getCategory());
            ps.setString(2, product.getTitle());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setString(5, product.getTexture());
            ps.setString(6, product.getWash());
            ps.setString(7, product.getPlace());
            ps.setString(8, product.getNote());
            ps.setString(9, product.getStory());
            ps.setString(10, product.getMainImage());
            return ps;
        }, keyHolder);
        int productId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        product.setId(productId);

        if (product.getColors() != null && !product.getColors().isEmpty()) {
            //logger.debug("Colors to insert for product {}: {}", productId, product.getColors());
            insertColors(productId, product.getColors());
        }

        if (product.getSizes() != null && !product.getSizes().isEmpty()) {
            //logger.debug("Sizes to insert for product {}: {}", productId, product.getSizes());
            insertSizes(productId, product.getSizes());
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            insertVariants(productId, product.getVariants());
        }

        if (product.getOtherImages() != null && !product.getOtherImages().isEmpty()) {
            insertImages(productId, product.getOtherImages());
        }
        logger.debug("Inserted product with ID: {}", productId);
    }

    private void insertColors(int productId, List<ProductColor> colors) {
        String sql = "INSERT INTO productcolors (product_id, name, code) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, colors, 100, (ps, color) -> {
            ps.setInt(1, productId);
            ps.setString(2, color.getName());
            ps.setString(3, color.getCode());
        });
        logger.debug("Inserted {} colors for product ID: {}", colors.size(), productId);
    }

    private void insertSizes(int productId, List<Product.ProductSize> sizes) {
        String sql = "INSERT INTO productsizes (product_id, size) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, sizes, 100, (ps, size) -> {
            ps.setInt(1, productId);
            ps.setString(2, size.getSize());
        });
        logger.debug("Inserted {} sizes for product ID: {}", sizes.size(), productId);
    }

    private void insertVariants(int productId, List<ProductVariant> variants) {
        String sql = "INSERT INTO productvariants (product_id, color_code, size, stock) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, variants, 100, (ps, variant) -> {
            ps.setInt(1, productId);
            ps.setString(2, variant.getColorCode());
            ps.setString(3, variant.getSize());
            ps.setInt(4, variant.getStock());
        });
        logger.debug("Inserted variants for product ID: {}", productId);
    }

    private void insertImages(int productId, List<String> images) {
        String sql = "INSERT INTO productimages (product_id, image_url) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, images, 100, (ps, imageUrl) -> {
            ps.setInt(1, productId);
            ps.setString(2, imageUrl);
        });
        logger.debug("Inserted images for product ID: {}", productId);
    }
    
    @Override
    public List<Map<String, Object>> getProducts(String category, int offset, int limit) {
        String sql;
        Object[] params;
        if ("all".equals(category)) {
            sql = "SELECT id, category, title, description, price, texture, wash, place, note, story, main_image FROM products LIMIT ? OFFSET ?";
            params = new Object[]{limit, offset};
        } else {
            sql = "SELECT id, category, title, description, price, texture, wash, place, note, story, main_image FROM products WHERE category = ? LIMIT ? OFFSET ?";
            params = new Object[]{category, limit, offset};
        }
        return jdbcTemplate.queryForList(sql, params);
    }

    @Override
    public int getTotalProductCount(String category) {
        String sql;
        Integer count;
        if ("all".equals(category)) {
            sql = "SELECT COUNT(*) FROM products";
            count = jdbcTemplate.queryForObject(sql, Integer.class);
        } else {
            sql = "SELECT COUNT(*) FROM products WHERE category = ?";
            count = jdbcTemplate.queryForObject(sql, Integer.class, category);
        }
        return (count != null) ? count : 0;
    }

    @Override
    public List<Map<String, Object>> getProductColors(int productId) {
        String sql = "SELECT name, code FROM productcolors WHERE product_id = ?";
        return jdbcTemplate.queryForList(sql, productId);
    }

    @Override
    public List<String> getProductSizes(int productId) {
        String sql = "SELECT DISTINCT size FROM productsizes WHERE product_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, productId);
    }

    @Override
    public List<Map<String, Object>> getProductVariants(int productId) {
        String sql = "SELECT color_code, size, stock FROM productvariants WHERE product_id = ?";
        return jdbcTemplate.queryForList(sql, productId);
    }

    @Override
    public List<String> getProductImages(int productId) {
        String sql = "SELECT image_url FROM productimages WHERE product_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, productId);
    }

    @Override
    public List<Map<String, Object>> searchProducts(String keyword, int offset, int limit) {
        String sql = "SELECT id, category, title, description, price, texture, wash, place, note, story, main_image " +
                "FROM products " +
                "WHERE title LIKE ? " +
                "LIMIT ? OFFSET ?";

        String searchKeyword = "%" + keyword + "%";

        return jdbcTemplate.queryForList(sql, searchKeyword, limit, offset);
    }

    @Override
    public int getSearchProductCount(String keyword) {
        String sql = "SELECT COUNT(*) FROM products WHERE title LIKE ?";

        String searchKeyword = "%" + keyword + "%";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, searchKeyword);
        return (count != null) ? count : 0;
    }

    @Override
    public List<Map<String, Object>> getProductById(int id) {
        String sql = "SELECT id, category, title, description, price, texture, wash, place, note, story, main_image " +
                "FROM products " +
                "WHERE id = ?";

        return jdbcTemplate.queryForList(sql, id);
    }

    @Override
    public Product.ProductVariant getProductVariant(int productId, String colorCode, String size) {
        String sql = "SELECT * FROM productvariants WHERE product_id = ? AND color_code = ? AND size = ?";

        List<Product.ProductVariant> variants = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Product.ProductVariant variant = new Product.ProductVariant();
                    variant.setId(rs.getInt("id"));
                    variant.setProductId(rs.getInt("product_id"));
                    variant.setColorCode(rs.getString("color_code"));
                    variant.setSize(rs.getString("size"));
                    variant.setStock(rs.getInt("stock"));
                    return variant;
                },
                productId, colorCode, size
        );

        return variants.isEmpty() ? null : variants.get(0);
    }

    @Override
    public void updateProductVariant(Product.ProductVariant variant) {
        String sql = "UPDATE productvariants SET stock = ? WHERE product_id = ? AND color_code = ? AND size = ?";
        jdbcTemplate.update(sql, variant.getStock(), variant.getProductId(), variant.getColorCode(), variant.getSize());
    }
}