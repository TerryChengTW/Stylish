package com.stylish.service;

import com.stylish.dao.OrderDao;
import com.stylish.dao.ProductDao;
import com.stylish.exception.InsufficientInventoryException;
import com.stylish.exception.PaymentException;
import com.stylish.model.Order;
import com.stylish.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    private final OrderDao orderDao;

    private final ProductDao productDao;

    private final TapPayService tapPayService;

    public OrderServiceImpl(OrderDao orderDao, ProductDao productDao, TapPayService tapPayService) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.tapPayService = tapPayService;
    }

    @Override
    @Transactional(rollbackFor = {InsufficientInventoryException.class, PaymentException.class})
    public String createOrder(int userId,String prime, Order order) throws InsufficientInventoryException, PaymentException {
        Map<String, Order.OrderItem> mergedItems = new HashMap<>();

        for (Order.OrderItem item : order.getList()) {
            String key = item.getId() + "-" + item.getColor().getCode() + "-" + item.getSize();
            if (mergedItems.containsKey(key)) {
                Order.OrderItem existingItem = mergedItems.get(key);
                existingItem.setQty(existingItem.getQty() + item.getQty());
            } else {
                mergedItems.put(key, item);
            }
        }
        order.setList(new ArrayList<>(mergedItems.values()));

        for (Order.OrderItem item : order.getList()) {
            Product.ProductVariant variant = productDao.getProductVariant(
                    Integer.parseInt(item.getId()),
                    item.getColor().getCode(),
                    item.getSize()
            );

            if (variant == null || variant.getStock() < item.getQty()) {
                throw new InsufficientInventoryException("商品庫存不足: " + item.getName());
            }

            int stockAtTime = variant.getStock();
            item.setStockAtTime(stockAtTime);

            variant.setStock(variant.getStock() - item.getQty());
            productDao.updateProductVariant(variant);
        }

        order.setUserId(userId);
        String orderNumber = orderDao.insertOrder(order);

        Map<String, Object> paymentResult = tapPayService.createPayment(prime, order);

        int status = (int) paymentResult.get("status");
        if (status != 0) {
            String errorMsg = (String) paymentResult.get("msg");
            logger.error("Payment failed for order {}: {}", orderNumber, errorMsg);
            throw new PaymentException("Payment failed: " + errorMsg);
        }

        orderDao.updateOrderStatus(orderNumber, "paid");

        return orderNumber;
    }
}