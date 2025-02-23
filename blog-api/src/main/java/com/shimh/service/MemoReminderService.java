package com.shimh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.Set;

@Service
public class MemoReminderService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String MEMO_KEY = "memos";

    // 每天检查一次
    @Scheduled(fixedRate = 86400000)  // 每天执行一次，单位是毫秒（86400000ms = 1天）
    public void checkMemos() {
        long currentTime = Instant.now().toEpochMilli();  // 当前时间戳
        long sevenDaysLater = currentTime + Duration.ofDays(7).toMillis();  // 当前时间 + 7天（单位是毫秒）

        // 获取所有提醒时间在7天后到期的备忘录（即7天内到期的备忘录）
        Set<String> upcomingMemos = redisTemplate.opsForZSet().rangeByScore(MEMO_KEY, currentTime, sevenDaysLater);

        if (upcomingMemos != null) {
            upcomingMemos.forEach(memo -> {
                // 提醒用户
                System.out.println("备忘录将在7天内到期提醒: " + memo);
                // 在此处触发推送通知或其他操作
            });
        }
    }
}
