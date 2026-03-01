package com.tom.message.template;

import com.tom.message.exception.MessageSendException;
import com.tom.message.model.MessageRequest;
import com.tom.message.model.MessageResponse;
import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
public abstract class AbstractMessageSender {
    protected final WebClient webClient;

    // 超时配置
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(30);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration RETRY_BACKOFF = Duration.ofSeconds(1);

    protected AbstractMessageSender(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 模板方法：定义发送消息的算法骨架
     */
    public Mono<MessageResponse> send(MessageRequest request) {
        return Mono.deferContextual(contextView -> {
            // 从Context中获取traceId
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);

            // 将traceId设置到MDC，确保当前线程的日志能打印traceId
            TraceIdUtil.putTraceIdToMDC(traceId);

            log.info("开始发送消息, traceId: {}, type: {}, recipient: {}",
                    traceId, request.getMessageType(), request.getRecipient());

            long startTime = System.currentTimeMillis();

            return validateRequest(request)
                    .doOnNext(validateReq -> log.debug("消息验证通过, traceId: {}", traceId))
                    .flatMap(this::doSend)
                    // 添加响应超时
                    .timeout(RESPONSE_TIMEOUT)
                    // 添加重试机制
                    .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, RETRY_BACKOFF)
                            .filter(this::isRetryableException)
                            .doBeforeRetry(retrySignal ->
                                    log.warn("重试发送消息, traceId: {}, attempt: {}, lastError: {}",
                                            traceId, retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()))
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                    new MessageSendException(getVendorName(), traceId,
                                            "重试次数耗尽: " + retrySignal.failure().getMessage(),
                                            MessageSendException.ErrorType.OTHER, retrySignal.failure())))
                    // 处理超时异常
                    .onErrorResume(TimeoutException.class, error ->
                            handleTimeoutError(request, error, traceId))
                    // 处理连接异常
                    .onErrorResume(ConnectException.class, error ->
                            handleConnectionError(request, error, traceId))
                    // 处理WebClient请求异常
                    .onErrorResume(WebClientRequestException.class, error ->
                            handleWebClientRequestError(request, error, traceId))
                    // 处理WebClient响应异常
                    .onErrorResume(WebClientResponseException.class, error ->
                            handleWebClientResponseError(request, error, traceId))
                    // 处理其他异常
                    .onErrorResume(Exception.class, error ->
                            handleOtherError(request, error, traceId))
                    .doOnNext(response -> {
                        long cost = System.currentTimeMillis() - startTime;
                        if (response.isSuccess()) {
                            log.info("消息发送成功, traceId: {}, vendor: {}, cost: {}ms",
                                    traceId, response.getVendor(), cost);
                        } else {
                            log.warn("消息发送失败, traceId: {}, vendor: {}, message: {}, cost: {}ms",
                                    traceId, response.getVendor(), response.getMessage(), cost);
                        }
                    })
                    .doFinally(signalType -> {
                        log.debug("消息发送处理完成, traceId: {}, signalType: {}", traceId, signalType);
                        TraceIdUtil.clearTraceIdFromMDC();
                    })
                    .contextWrite(ctx -> ctx.put(TraceIdUtil.TRACE_ID, traceId));
        });
    }

    /**
     * 判断是否为可重试的异常
     */
    private boolean isRetryableException(Throwable error) {
        return error instanceof TimeoutException ||
                error instanceof ConnectException ||
                (error instanceof WebClientRequestException &&
                        error.getCause() instanceof ConnectException) ||
                (error instanceof WebClientResponseException &&
                        ((WebClientResponseException) error).getStatusCode().is5xxServerError());
    }

    /**
     * 处理超时错误
     */
    private Mono<MessageResponse> handleTimeoutError(MessageRequest request, Throwable error, String traceId) {
        log.error("消息发送超时, traceId: {}, vendor: {}, error: {}",
                traceId, getVendorName(), error.getMessage());

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("发送超时: " + error.getMessage());
        response.setVendor(getVendorName());
        response.setTraceId(traceId);
        return Mono.just(response);
    }

    /**
     * 处理连接错误
     */
    private Mono<MessageResponse> handleConnectionError(MessageRequest request, Throwable error, String traceId) {
        log.error("消息发送连接失败, traceId: {}, vendor: {}, error: {}",
                traceId, getVendorName(), error.getMessage());

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("连接失败: " + error.getMessage());
        response.setVendor(getVendorName());
        response.setTraceId(traceId);
        return Mono.just(response);
    }

    /**
     * 处理WebClient请求错误
     */
    private Mono<MessageResponse> handleWebClientRequestError(MessageRequest request,
                                                              WebClientRequestException error,
                                                              String traceId) {
        log.error("WebClient请求异常, traceId: {}, vendor: {}, error: {}",
                traceId, getVendorName(), error.getMessage(), error);

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("请求异常: " + error.getMessage());
        response.setVendor(getVendorName());
        response.setTraceId(traceId);
        return Mono.just(response);
    }

    /**
     * 处理WebClient响应错误
     */
    private Mono<MessageResponse> handleWebClientResponseError(MessageRequest request,
                                                               WebClientResponseException error,
                                                               String traceId) {
        log.error("WebClient响应异常, traceId: {}, vendor: {}, status: {}, body: {}, error: {}",
                traceId, getVendorName(), error.getStatusCode(), error.getResponseBodyAsString(), error.getMessage());

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("响应异常: " + error.getStatusCode() + " - " + error.getMessage());
        response.setVendor(getVendorName());
        response.setTraceId(traceId);
        return Mono.just(response);
    }

    /**
     * 处理其他错误
     */
    private Mono<MessageResponse> handleOtherError(MessageRequest request, Throwable error, String traceId) {
        log.error("消息发送其他异常, traceId: {}, vendor: {}, error: {}",
                traceId, getVendorName(), error.getMessage(), error);

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("发送异常: " + error.getMessage());
        response.setVendor(getVendorName());
        response.setTraceId(traceId);
        return Mono.just(response);
    }

    /**
     * 验证请求（子类可重写）
     */
    protected Mono<MessageRequest> validateRequest(MessageRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("消息内容不能为空"));
        }
        if (request.getRecipient() == null || request.getRecipient().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("接收者不能为空"));
        }
        return Mono.just(request);
    }

    /**
     * 具体的发送逻辑（由子类实现）
     */
    protected abstract Mono<MessageResponse> doSend(MessageRequest request);

    /**
     * 获取厂商名称
     */
    protected abstract String getVendorName();

    /**
     * 获取secretKey（由子类提供）
     */
    protected abstract String getSecretKey();
}
