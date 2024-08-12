package com.stylish.service;

import com.stylish.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TapPayServiceImpl implements TapPayService {
    private static final Logger logger = LoggerFactory.getLogger(TapPayServiceImpl.class);


    @Value("${tappay.partner-key}")
    private String partnerKey;

    @Value("${tappay.merchant-id}")
    private String merchantId;

    @Value("${tappay.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public TapPayServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> createPayment(String prime, Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", partnerKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prime", prime);
        requestBody.put("partner_key", partnerKey);
        requestBody.put("merchant_id", merchantId);
        requestBody.put("amount", order.getTotal().intValue());
        requestBody.put("currency", "TWD");
        requestBody.put("details", "Stylish Order " + order.getOrderNumber());
        requestBody.put("cardholder", createCardholderInfo(order));
        requestBody.put("remember", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        logger.debug("Sending payment request to TapPay: {}", requestBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

        logger.debug("Received response from TapPay: {}", response);

        return response;
    }

    private Map<String, Object> createCardholderInfo(Order order) {
        Map<String, Object> cardholderInfo = new HashMap<>();
        cardholderInfo.put("phone_number", order.getRecipient().getPhone());
        cardholderInfo.put("name", order.getRecipient().getName());
        cardholderInfo.put("email", order.getRecipient().getEmail());
        cardholderInfo.put("address", order.getRecipient().getAddress());
        return cardholderInfo;
    }
}