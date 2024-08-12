package com.stylish.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminUploadController {

    @GetMapping("/admin/product.html")
    public String productPage() {
        return "admin/product";
    }

    @GetMapping("/admin/campaign.html")
    public String campaignPage() {
        return "admin/campaign";
    }

    @GetMapping("/admin/checkout.html")
    public String checkoutPage() { return "admin/checkout"; }
}