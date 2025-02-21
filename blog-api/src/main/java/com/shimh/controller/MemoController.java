package com.shimh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class MemoController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String MEMO_KEY = "memos";

    // 添加备忘录
    @PostMapping("/memo")
    public String addMemo(@RequestParam String memoText, @RequestParam String reminderTime) {
        long reminderTimestamp = Instant.parse(reminderTime).toEpochMilli();

        // 将备忘录添加到 Redis Sorted Set
        redisTemplate.opsForZSet().add(MEMO_KEY, memoText, reminderTimestamp);

        return "备忘录已添加";
    }

    // 获取未过期的备忘录
    @GetMapping("/memos")
    public List<String> getMemos() {
        List<String> memos = new ArrayList<>();

        // 获取所有备忘录（不筛选）
        Set<String> allMemos = redisTemplate.opsForZSet().range(MEMO_KEY, 0, -1);

        // 打印查询到的备忘录
        System.out.println("查询到的所有备忘录: " + allMemos);

        // 如果有备忘录，返回它们
        if (allMemos != null) {
            for (String memo : allMemos) {
                memos.add(memo);
            }
        }

        return memos;
    }

}
