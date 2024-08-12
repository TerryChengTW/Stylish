package com.stylish.service;

import com.stylish.dao.ReportDao;
import com.stylish.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final ReportDao reportDao;

    public ReportService(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public List<Map<String, Object>> generatePaymentReport() {
        logger.info("Generating payment report");
        List<Order> orders = reportDao.getAllOrders();
        Map<Integer, BigDecimal> userTotals = new LinkedHashMap<>();

        for (Order order : orders) {
            userTotals.merge(order.getUserId(), order.getTotal(), BigDecimal::add);
        }

        List<Map<String, Object>> report = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : userTotals.entrySet()) {
            Map<String, Object> userReport = new LinkedHashMap<>();
            userReport.put("user_id", entry.getKey());
            userReport.put("total_payment", entry.getValue());
            report.add(userReport);
        }

        logger.info("Payment report generated successfully");
        return report;
    }
}