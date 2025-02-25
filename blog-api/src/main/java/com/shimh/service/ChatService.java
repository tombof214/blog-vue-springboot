package com.shimh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    private final RestTemplate restTemplate;

    @Autowired
    public ChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callRemoteService(String message) {
        String url = "http://localhost:3001/api/v1/workspace/test/chat";

        // 创建请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", message);
        requestBody.put("mode", "chat");
        System.out.println(message);
        // 使用 Hutool 转换 Map 为 JSON 字符串
        String jsonRequestBody = JSONUtil.toJsonStr(requestBody);

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer ZGVZH02-1GBM937-QVBY2GP-W0QYQD5");

        // 创建请求实体
        HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

        // 发送 POST 请求并获取响应
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 返回响应体内容
        return response.getBody();
    }
}

