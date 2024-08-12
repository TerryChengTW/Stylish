package com.stylish.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacebookLoginController {

    @Value("${facebook.app.id}")
    private String facebookAppId;

    @GetMapping("/facebook-login")
    public String facebookLoginPage(Model model) {
        model.addAttribute("fbAppId", facebookAppId);
        return "facebook-login";
    }
}