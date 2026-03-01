package com.tom.message.controller;

import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    public Mono<Map<String, Object>> health() {
        return Mono.deferContextual(contextView -> {
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);

            Map<String, Object> status = new HashMap<>();
            status.put("status", "UP");
            status.put("timestamp", System.currentTimeMillis());
            status.put("traceId", traceId);

            // 添加各厂商连接状态（这里可以添加实际的心跳检测）
            Map<String, String> vendors = new HashMap<>();
            vendors.put("aliyun", "unknown");
            vendors.put("tencent", "unknown");
            vendors.put("huawei", "unknown");
            status.put("vendors", vendors);

            log.info("健康检查, traceId: {}, status: {}", traceId, status);

            return Mono.just(status);
        });
    }
}
