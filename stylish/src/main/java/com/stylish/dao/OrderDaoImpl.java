package com.stylish.dao;

import com.stylish.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

@Repository
public class OrderDaoImpl implements OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String insertOrder(Order order) {
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);

        String sql = "INSERT INTO orders (order_number, user_id, shipping, payment, subtotal, freight, total, " +
                "recipient_name, recipient_phone, recipient_email, recipient_address, recipient_time, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderNumber);
            ps.setInt(2, order.getUserId());
            ps.setString(3, order.getShipping());
            ps.setString(4, order.getPayment());
            ps.setBigDecimal(5, order.getSubtotal());
            ps.setBigDecimal(6, order.getFreight());
            ps.setBigDecimal(7, order.getTotal());
            ps.setString(8, order.getRecipient().getName());
            ps.setString(9, order.getRecipient().getPhone());
            ps.setString(10, order.getRecipient().getEmail());
            ps.setString(11, order.getRecipient().getAddress());
            ps.setString(12, order.getRecipient().getTime());
            ps.setString(13, "unpaid");  // 初始狀態設為 "unpaid"
            return ps;
        }, keyHolder);

        @SuppressWarnings("null")
        int orderId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        insertOrderItems(orderId, order);

        return orderNumber;
    }

    private void insertOrderItems(int orderId, Order order) {
        String sql = "INSERT INTO orderitems (order_id, product_id, product_name, price, color_code, " +
                "color_name, size, quantity, stock_at_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (Order.OrderItem item : order.getList()) {
            jdbcTemplate.update(sql,
                    orderId,
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getColor().getCode(),
                    item.getColor().getName(),
                    item.getSize(),
                    item.getQty(),
                    item.getStockAtTime()
            );
        }
    }

    @Override
    public void updateOrderStatus(String orderNumber, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_number = ?";
        jdbcTemplate.update(sql, status, orderNumber);
    }

    private String generateOrderNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
    }
}