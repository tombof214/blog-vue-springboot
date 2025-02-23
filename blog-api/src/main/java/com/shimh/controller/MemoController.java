package com.shimh.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.shimh.entity.MemoDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MemoController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String MEMO_KEY = "memo";

    // 添加备忘录
    @PostMapping("/memo")
    public String addMemo(@RequestBody MemoDetails memoDetails) {
        // 生成唯一的ID
        String memoId = UUID.randomUUID().toString();  // Create a unique ID for the memo

        // 从 memoDetails 对象中获取数据
        String title = memoDetails.getTitle();
        String body = memoDetails.getBody();
        String createTime = memoDetails.getCreatedDate();
        String reminderTime = memoDetails.getDueDate();
        // 将提醒时间解析为时间戳
        long reminderTimestamp = Instant.parse(reminderTime).toEpochMilli();
        System.out.println(createTime);
        // 设置唯一的ID到memoDetails中
        memoDetails.setId(memoId); // Set the ID in the MemoDetails
        String memoDetailsJson = JSONUtil.toJsonStr(memoDetails); // Hutool JSON 工具类
        System.out.println(memoDetailsJson);
        // 将备忘录存入 Redis Hash 中, 以 memoId 为 Key, MemoDetails 为 Value
        redisTemplate.opsForHash().put(MEMO_KEY, memoId, memoDetailsJson);

        // 使用 ZSet 存储提醒时间，以便按时间排序
        redisTemplate.opsForZSet().add(MEMO_KEY + ":zset", memoId, reminderTimestamp);

        return "备忘录已添加";
    }

    // 获取单个备忘录
    @GetMapping("/memo/{id}")
    public MemoDetails getMemoById(@PathVariable String id) {
        // 从 Redis 中获取备忘录对象
        String memoDetailsJson = (String) redisTemplate.opsForHash().get(MEMO_KEY, id);
        MemoDetails memoDetails = JSONUtil.toBean(memoDetailsJson, MemoDetails.class);
        if (memoDetails == null) {
            throw new RuntimeException("备忘录未找到");
        }

        return memoDetails;
    }

    // 获取未过期的备忘录
    @GetMapping("/memos")
    public List<MemoDetails> getMemos() {
        List<MemoDetails> memoDetailsList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        // 获取所有备忘录的 ID 和时间戳
        Set<String> allMemoIds = redisTemplate.opsForZSet().range(MEMO_KEY + ":zset", 0, -1);

        if (allMemoIds != null) {
            for (String memoId : allMemoIds) {
                // 获取备忘录的提醒时间
                Double reminderTimeDouble = redisTemplate.opsForZSet().score(MEMO_KEY + ":zset", memoId);

                if (reminderTimeDouble != null) {
                    long reminderTime = reminderTimeDouble.longValue();

                    // 只返回未过期的备忘录
                    if (reminderTime > currentTime) {
                        String memoDetailsJson = (String) redisTemplate.opsForHash().get(MEMO_KEY, memoId);
                        MemoDetails memoDetails = JSONUtil.toBean(memoDetailsJson, MemoDetails.class);
                       // MemoDetails memoDetails = (MemoDetails) redisTemplate.opsForHash().get(MEMO_KEY, memoId);
                        memoDetailsList.add(memoDetails);
                    }
                }
            }
        }

        return memoDetailsList;
    }
}
