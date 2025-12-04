package com.ohgiraffers.timedeal.core.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUpBase() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushDb();
    }
}
