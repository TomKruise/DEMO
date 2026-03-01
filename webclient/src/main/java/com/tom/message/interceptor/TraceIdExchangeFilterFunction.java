package com.tom.message.interceptor;

import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
public class TraceIdExchangeFilterFunction implements ExchangeFilterFunction {
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return Mono.deferContextual(contextView -> {
            // 从Context中获取traceId
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);

            // 将traceId设置到MDC，确保当前线程的日志能打印traceId
            TraceIdUtil.putTraceIdToMDC(traceId);

            // 在请求头中添加traceId
            ClientRequest newRequest = ClientRequest.from(request)
                    .header("X-Trace-Id", traceId)
                    .build();

            log.debug("WebClient发送请求, traceId: {}, url: {}, method: {}",
                    traceId, newRequest.url(), newRequest.method());

            return next.exchange(newRequest)
                    .doOnNext(resp -> log.debug("WebClient收到响应, traceId: {}, status: {}", traceId, resp.statusCode()))
                    .doOnError(err-> log.error("WebClient请求失败, traceId: {}, error: {}", traceId, err.getMessage()))
                    .doFinally(signalType -> {
                        log.debug("WebClient请求完成, traceId: {}, signalType: {}", traceId, signalType);
                        TraceIdUtil.clearTraceIdFromMDC();
                    })
                    // 传递Context
                    .contextWrite(ctx -> ctx.put(TraceIdUtil.TRACE_ID, traceId));
        });
    }
}
