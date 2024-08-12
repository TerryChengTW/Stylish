package com.stylish.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<List<Object>> rateLimiterScript;

    @Autowired
    public RateLimiterService(@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.rateLimiterScript = RedisScript.of(
                "local key = KEYS[1] " +
                        "local limit = tonumber(ARGV[1]) " +
                        "local window = tonumber(ARGV[2]) " +
                        "local current = redis.call('TIME') " +
                        "local currentMs = tonumber(current[1]) * 1000 + math.floor(tonumber(current[2]) / 1000) " +
                        "redis.call('ZREMRANGEBYSCORE', key, 0, currentMs - window) " +
                        "redis.call('ZADD', key, currentMs, tostring(currentMs)) " +
                        "redis.call('EXPIRE', key, math.ceil(window / 1000)) " +
                        "local count = redis.call('ZCARD', key) " +
                        "local windowStart = currentMs " +
                        "if count > 0 then " +
                        "    windowStart = tonumber(redis.call('ZRANGE', key, 0, 0, 'WITHSCORES')[2]) " +
                        "end " +
                        "return {count, currentMs, windowStart}",
                (Class<List<Object>>)(Class<?>) List.class
        );
    }

    public boolean allowRequest(String key, int limit, int windowInMillis) {
        String redisKey = "sliding_window:" + key;

        List<Object> result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(redisKey),
                String.valueOf(limit),
                String.valueOf(windowInMillis)
        );

        long count = ((Number) result.get(0)).longValue();
        long currentTimeMs = ((Number) result.get(1)).longValue();
        long windowStartMs = ((Number) result.get(2)).longValue();

        boolean allowed = count <= limit;
        logger.info("Request for key: {}, Count: {}, Allowed: {}, Window Start: {}, Current Time: {}, Time in Window: {}",
                key, count, allowed, windowStartMs, currentTimeMs, currentTimeMs - windowStartMs);

        return allowed;
    }
}