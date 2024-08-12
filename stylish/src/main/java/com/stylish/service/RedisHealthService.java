package com.stylish.service;

import org.springframework.stereotype.Service;

@Service
public class RedisHealthService {
    private boolean redisOffline = false;
    private boolean redisJustReconnected = false;

    public boolean isRedisOffline() {
        return redisOffline;
    }

    public void setRedisOffline(boolean redisOffline) {
        this.redisOffline = redisOffline;
    }

    public boolean isRedisJustReconnected() {
        return redisJustReconnected;
    }

    public void setRedisJustReconnected(boolean redisJustReconnected) {
        this.redisJustReconnected = redisJustReconnected;
    }

    public void checkAndSetRedisStatus(boolean status) {
        if (status) {
            if (redisOffline) {
                redisOffline = false;
                redisJustReconnected = true;
            } else {
                redisJustReconnected = false;
            }
        } else {
            redisOffline = true;
            redisJustReconnected = false;
        }
    }
}