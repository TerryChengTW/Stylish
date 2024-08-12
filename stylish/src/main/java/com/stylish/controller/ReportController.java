package com.stylish.controller;

import com.stylish.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/report")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getPaymentReport() {
        try {
            List<Map<String, Object>> paymentReport = reportService.generatePaymentReport();
            return successResponse(paymentReport);
        } catch (Exception e) {
            logger.error("Error generating payment report", e);
            return errorResponse("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> successResponse(List<Map<String, Object>> data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> errorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put("error", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}