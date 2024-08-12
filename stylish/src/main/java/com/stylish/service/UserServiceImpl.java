package com.stylish.service;

import com.stylish.dao.UserDao;
import com.stylish.exception.BadRequestException;
import com.stylish.exception.ForbiddenException;
import com.stylish.model.User;
import com.stylish.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    private static final String CACHE_KEY_PREFIX = "user:profile:";
    private static final long CACHE_TTL = 60;

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisHealthService redisHealthService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder,
                           RedisTemplate<String, Object> redisTemplate,
                           RedisHealthService redisHealthService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.redisHealthService = redisHealthService;
    }

    @Override
    public User registerUser(String name, String email, String password) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new BadRequestException("Missing required fields");
        }

        String emailRegex = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.]+$";
        if (!email.matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }

        if (userDao.getUserByEmail(email) != null) {
            throw new ForbiddenException("Email already registered");
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPicture(" ");
        newUser.setProvider("native");

        userDao.insertUser(newUser);
        return newUser;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserFromCacheOrDatabase(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Set<String> roles = new HashSet<>(userDao.getUserRoles(user.getId()));

        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }

        user.setRoles(roles);

        return new UserPrincipal(user);
    }

    public User getUserFromCacheOrDatabase(String email) {
        logger.info("Attempting to get user for email: {}", email);
        String cacheKey = CACHE_KEY_PREFIX + email;

        if (redisHealthService.isRedisJustReconnected()) {
            logger.info("Redis just reconnected. Clearing all user cache.");
            clearRedisCache();
            redisHealthService.checkAndSetRedisStatus(true);
        }

        if (redisHealthService.isRedisOffline()) {
            logger.info("Redis is offline. Retrieving user from database.");
            return userDao.getUserByEmail(email);
        }

        try {
            logger.info("Checking cache for email: {}", email);
            Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
            if (cachedObject instanceof User) {
                logger.info("User found in cache for email: {}", email);
                return (User) cachedObject;
            }
        } catch (Exception e) {
            logger.error("Error accessing Redis cache for email: {}", email, e);
            redisHealthService.setRedisOffline(true);
            return userDao.getUserByEmail(email);
        }

        logger.info("User not found in cache, retrieving from database for email: {}", email);
        User user = userDao.getUserByEmail(email);

        if (user != null) {
            try {
                logger.info("Retrieving roles for user ID: {}", user.getId());
                Set<String> roles = new HashSet<>(userDao.getUserRoles(user.getId()));
                logger.info("Roles retrieved for user ID: {}: {}", user.getId(), roles);
                user.setRoles(roles);

                try {
                    redisTemplate.opsForValue().set(cacheKey, user, CACHE_TTL, TimeUnit.SECONDS);
                    logger.info("Stored user with roles in cache for email: {}", email);
                } catch (Exception e) {
                    logger.error("Error storing user with roles in Redis cache for email: {}", email, e);
                    redisHealthService.setRedisOffline(true);
                }
            } catch (Exception e) {
                logger.error("Error retrieving or setting roles for user ID: {}", user.getId(), e);
            }
        } else {
            logger.warn("User not found in database for email: {}", email);
        }
        return user;
    }

    private void clearRedisCache() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            logger.info("Cleared all user cache keys");
        } catch (Exception e) {
            logger.error("Error clearing Redis cache", e);
        }
    }

    @Override
    public User processOAuthPostLogin(String email, String name, String picture, String provider) {
        User existingUser = userDao.getUserByEmail(email);
        if (existingUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPicture(picture);
            newUser.setProvider(provider);
            userDao.insertUser(newUser);
            return newUser;
        }
        return existingUser;
    }
}