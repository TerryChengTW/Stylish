package com.stylish.dao;

import com.stylish.model.Order;

public interface OrderDao {
    String insertOrder(Order order);
    void updateOrderStatus(String orderNumber, String status);
}