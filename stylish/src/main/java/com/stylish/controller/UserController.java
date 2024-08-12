package com.stylish.controller;

import com.stylish.exception.BadRequestException;
import com.stylish.exception.ForbiddenException;
import com.stylish.model.User;
import com.stylish.security.UserPrincipal;
import com.stylish.service.UserService;
import com.stylish.util.JwtUtil;
import com.stylish.service.FacebookClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final FacebookClient facebookClient;

    public UserController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil, FacebookClient facebookClient) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.facebookClient = facebookClient;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Map<String, String> signUpRequest) {
        String name = signUpRequest.get("name");
        String email = signUpRequest.get("email");
        String password = signUpRequest.get("password");

        try {
            User newUser = userService.registerUser(name, email, password);
            String token = jwtUtil.createToken(newUser.getEmail(), new HashMap<>());
            return successResponse(token, jwtUtil.defaultExpire / 1000, newUser);
        } catch (BadRequestException e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        } catch (ForbiddenException e) {
            return errorResponse(e.getMessage(), HttpStatus.FORBIDDEN); // 403
        } catch (Exception e) {
            return errorResponse("Server Error", HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> signInRequest, HttpServletRequest request) {
        String provider = signInRequest.get("provider");

        if (provider == null || provider.isEmpty()) {
            return errorResponse("Provider is required", HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<?> response;
        if ("native".equals(provider)) {
            response = handleNativeSignIn(signInRequest);
        } else if ("facebook".equals(provider)) {
            response = handleFacebookSignIn(signInRequest);
        } else {
            return errorResponse("Invalid provider", HttpStatus.BAD_REQUEST);
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                logger.info("Session ID: {}", session.getId());
                logger.info("Session Creation Time: {}", new Date(session.getCreationTime()));
                logger.info("Session Last Accessed Time: {}", new Date(session.getLastAccessedTime()));
            }
        }

        return response;
    }

    private ResponseEntity<?> handleNativeSignIn(Map<String, String> signInRequest) {
        String email = signInRequest.get("email");
        String password = signInRequest.get("password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return errorResponse("Missing required fields", HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.user();

            String token = jwtUtil.createToken(user.getEmail(), new HashMap<>());
            return successResponse(token, jwtUtil.defaultExpire / 1000, user);
        } catch (AuthenticationException e) {
            return errorResponse("Sign In Failed", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return errorResponse("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleFacebookSignIn(Map<String, String> signInRequest) {
        String accessToken = signInRequest.get("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            return errorResponse("Missing Facebook access token", HttpStatus.BAD_REQUEST);
        }

        try {
            logger.debug("Attempting to get user from Facebook with access token: {}", accessToken);
            User fbUser = facebookClient.getUser(accessToken);
            logger.debug("User obtained from Facebook: {}", fbUser);

            User user = userService.processOAuthPostLogin(fbUser.getEmail(), fbUser.getName(), fbUser.getPicture(), fbUser.getProvider());

            String token = jwtUtil.createToken(user.getEmail(), new HashMap<>());

            return successResponse(token, jwtUtil.defaultExpire / 1000, user);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Invalid Facebook access token")) {
                logger.error("Invalid Facebook access token", e);
                return errorResponse("Invalid Facebook access token", HttpStatus.FORBIDDEN);
            }
            logger.error("Facebook authentication failed", e);
            return errorResponse("Facebook authentication failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return errorResponse("No token provided", HttpStatus.UNAUTHORIZED);
        }
        if (!(authentication.getPrincipal() instanceof UserPrincipal)) {
            return errorResponse("Wrong token", HttpStatus.FORBIDDEN);
        }
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.user();
            return profileResponse(user);
        } catch (Exception e) {
            return errorResponse("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> successResponse(String token, long expireTime, User user) {
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("id", user.getId());
        userData.put("provider", user.getProvider());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("picture", user.getPicture());

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("access_token", token);
        responseData.put("access_expired", expireTime);
        responseData.put("user", userData);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", responseData);

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> profileResponse(User user) {
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("provider", user.getProvider());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("picture", user.getPicture());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", userData);

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> errorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully.");
    }
}