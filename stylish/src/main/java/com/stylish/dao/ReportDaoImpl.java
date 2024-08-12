package com.stylish.dao;

import com.stylish.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReportDaoImpl implements ReportDao {

    private final JdbcTemplate jdbcTemplate;

    public ReportDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT user_id, total FROM orders";
        return jdbcTemplate.query(sql, this::mapRowToOrder);
    }

    private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setUserId(rs.getInt("user_id"));
        order.setTotal(rs.getBigDecimal("total"));
        return order;
    }
}