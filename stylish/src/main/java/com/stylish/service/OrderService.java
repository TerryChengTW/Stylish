package com.stylish.service;

import com.stylish.exception.PaymentException;
import com.stylish.model.Order;
import com.stylish.exception.InsufficientInventoryException;

public interface OrderService {
    String createOrder(int userId, String prime, Order order) throws InsufficientInventoryException, PaymentException;
}