package com.tom.message.filter;

import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
public class TraceIdFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 从请求头中获取traceId，如果没有则生成新的
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdUtil.generateTraceId();
        }

        // 将traceId设置到exchange的属性中，方便后续获取
        exchange.getAttributes().put(TraceIdUtil.TRACE_ID, traceId);

        // 将traceId设置到MDC中，确保当前线程的日志能打印traceId
        TraceIdUtil.putTraceIdToMDC(traceId);

        log.info("请求开始, traceId: {}, method: {}, path: {}",
                traceId, exchange.getRequest().getMethod(), exchange.getRequest().getPath());

        String finalTraceId = traceId;
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    log.info("请求结束, traceId: {}, signalType: {}", finalTraceId, signalType);
                    TraceIdUtil.clearTraceIdFromMDC();
                })
                .contextWrite(Context.of(TraceIdUtil.TRACE_ID, traceId));
    }

}
