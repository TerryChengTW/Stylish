package com.stylish.service;

import com.stylish.model.FacebookUser;
import com.stylish.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FacebookClient {

    private static final Logger logger = LoggerFactory.getLogger(FacebookClient.class);

    private final RestTemplate restTemplate;
    private final String appId;
    private final String appSecret;

    public FacebookClient(RestTemplate restTemplate,
                          @Value("${facebook.app.id}") String appId,
                          @Value("${facebook.app.secret}") String appSecret) {
        this.restTemplate = restTemplate;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public User getUser(String accessToken) {
        if (!isTokenValid(accessToken)) {
            throw new RuntimeException("Invalid Facebook access token");
        }

        String url = String.format("https://graph.facebook.com/me?fields=id,name,email,picture&access_token=%s", accessToken);
        try {
            FacebookUser fbUser = restTemplate.getForObject(url, FacebookUser.class);
            if (fbUser == null) {
                logger.error("Received null user from Facebook API");
                throw new RuntimeException("Failed to get user info from Facebook");
            }
            logger.info("Successfully retrieved user info from Facebook: {}", fbUser);
            return convertToUser(fbUser);
        } catch (Exception e) {
            logger.error("Error while fetching user info from Facebook", e);
            throw new RuntimeException("Failed to get user info from Facebook", e);
        }
    }

    private boolean isTokenValid(String accessToken) {
        String debugUrl = String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s|%s", accessToken, appId, appSecret);
        try {
            logger.debug("Validating token with URL: {}", debugUrl);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(debugUrl, Map.class);

            if (response == null || !response.containsKey("data")) {
                logger.error("Invalid response from Facebook debug token API");
                return false;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("data");

            if (data == null || !data.containsKey("is_valid")) {
                logger.error("Invalid data from Facebook debug token API");
                return false;
            }

            boolean isValid = (boolean) data.get("is_valid");
            logger.debug("Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Error while validating access token", e);
            return false;
        }
    }


    private User convertToUser(FacebookUser fbUser) {
        User user = new User();
        user.setName(fbUser.getName());
        user.setEmail(fbUser.getEmail());
        user.setPicture(fbUser.getPictureUrl());
        user.setProvider("facebook");
        return user;
    }
}
