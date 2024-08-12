package com.stylish.dao;

import com.stylish.model.Order;

import java.util.List;

public interface ReportDao {
    List<Order> getAllOrders();
}
