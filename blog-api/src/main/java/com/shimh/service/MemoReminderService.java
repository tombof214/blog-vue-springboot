package com.shimh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.Set;

@Component
public class MemoReminderService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String MEMO_KEY = "memos";

    // 每天检查一次
    @Scheduled(fixedRate = 86400000)  // 每天执行一次，单位是毫秒（86400000ms = 1天）
    public void checkMemos() {
        long currentTime = Instant.now().toEpochMilli();  // 当前时间戳

        // 获取所有提醒时间在当前时间之前的备忘录
        Set<String> expiredMemos = redisTemplate.opsForZSet().rangeByScore(MEMO_KEY, 0, currentTime);

        if (expiredMemos != null) {
            expiredMemos.forEach(memo -> {
                // 提醒用户
                System.out.println("备忘录到期提醒: " + memo);
                // 在此处触发推送通知或其他操作
            });

            // 从 Redis 中移除已过期的备忘录
            redisTemplate.opsForZSet().removeRangeByScore(MEMO_KEY, 0, currentTime);
        }
    }
}
