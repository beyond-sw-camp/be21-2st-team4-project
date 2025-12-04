package com.ohgiraffers.timedeal.core.domain;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StringRedisTemplate redisTemplate;

    public StockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean decreaseStock(Long promotionId, int quantity) {
        String key = "stock:" + promotionId;

        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return false;

        int stock = Integer.parseInt(value);

        if (stock < quantity) {
            return false;
        }

        redisTemplate.opsForValue().decrement(key, quantity);
        return true;
    }
}
