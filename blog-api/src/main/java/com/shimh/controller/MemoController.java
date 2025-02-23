package com.shimh.controller;

import cn.hutool.core.date.DateUtil;
import com.shimh.entity.MemoDetails;
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
    public String addMemo(@RequestParam("memoText") String memoText, @RequestParam String reminderTime) {
        long reminderTimestamp = Instant.parse(reminderTime).toEpochMilli();

        // 将备忘录添加到 Redis Sorted Set
        redisTemplate.opsForZSet().add(MEMO_KEY, memoText, reminderTimestamp);

        return "备忘录已添加";
    }

    // 获取未过期的备忘录
    @GetMapping("/memos")
    public List<MemoDetails> getMemos() {
        List<MemoDetails> memoDetailsList = new ArrayList<>();

        // 获取当前时间
        long currentTime = System.currentTimeMillis();

        // 获取所有备忘录（不筛选）
        Set<String> allMemos = redisTemplate.opsForZSet().range(MEMO_KEY, 0, -1);

        // 打印查询到的所有备忘录
        System.out.println("查询到的所有备忘录: " + allMemos);

        // 如果有备忘录，筛选出未过期的
        if (allMemos != null) {
            for (String memo : allMemos) {
                // 备忘录的提醒时间存在于Redis中，是一个Double类型的时间戳，已存储在zset的分数中。
                // 直接根据zset的分数（即时间戳）来筛选
                Double reminderTimeDouble = redisTemplate.opsForZSet().score(MEMO_KEY, memo);

                // 检查是否返回了一个有效的时间戳
                if (reminderTimeDouble != null) {
                    long reminderTime = reminderTimeDouble.longValue(); // 转换为long类型

                    // 只返回未过期的备忘录
                    if (reminderTime > currentTime) {
                        // 将备忘录名称和到期时间一同加入返回列表
                        // 使用 Hutool 转换时间戳为日期格式字符串
                        String reminderDate = DateUtil.format(DateUtil.date(reminderTime), "yyyy-MM-dd HH:mm:ss");
                        memoDetailsList.add(new MemoDetails(memo, reminderDate));
                    }
                }
            }
        }

        return memoDetailsList;
    }
}
