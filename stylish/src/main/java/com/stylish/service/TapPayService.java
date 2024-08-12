package com.stylish.service;

import com.stylish.model.Order;

import java.util.Map;

public interface TapPayService {
    Map<String, Object> createPayment(String prime, Order order);
}