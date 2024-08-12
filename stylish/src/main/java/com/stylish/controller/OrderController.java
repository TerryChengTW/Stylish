package com.stylish.controller;

import com.stylish.exception.InsufficientInventoryException;
import com.stylish.exception.PaymentException;
import com.stylish.model.CheckoutRequest;
import com.stylish.service.OrderService;
import com.stylish.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int userId = userPrincipal.user().getId();

        try {
            String orderNumber = orderService.createOrder(userId, checkoutRequest.getPrime(), checkoutRequest.getOrder());
            Map<String, Object> response = new HashMap<>();
            response.put("data", Map.of("number", orderNumber));
            return ResponseEntity.ok(response);
        } catch (InsufficientInventoryException e) {
            logger.error("Insufficient inventory: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (PaymentException e) {
            logger.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of("error", e.getMessage()));
        }
    }
}