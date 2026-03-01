package com.tom.message.sender;

import com.tom.message.model.MessageRequest;
import com.tom.message.model.MessageResponse;
import com.tom.message.template.AbstractMessageSender;
import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;

@Slf4j
public class AliyunSmsSender extends AbstractMessageSender {

    private static final String VENDOR_NAME = "阿里云";
    private static final String SECRET_KEY = "aliyun-secret-key-123456";

    // 阿里云特定的超时配置
    private static final Duration ALIYUN_CONNECTION_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration ALIYUN_RESPONSE_TIMEOUT = Duration.ofSeconds(10);

    public AliyunSmsSender(WebClient webClient) {
        super(webClient);
    }
    @Override
    protected Mono<MessageResponse> doSend(MessageRequest request) {
        return Mono.deferContextual(contextView -> {
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);

            // 将traceId设置到MDC，确保当前线程的日志能打印traceId
            TraceIdUtil.putTraceIdToMDC(traceId);

            log.info("调用阿里云短信API, traceId: {}, recipient: {}", traceId, request.getRecipient());

            return webClient.post()
                    .uri("https://api.aliyun.com/sms/send")
                    .header("Authorization", "Bearer " + getSecretKey())
                    .header("X-Trace-Id", traceId)
                    .bodyValue(new AliyunSmsRequest(request.getRecipient(), request.getContent()))
                    .retrieve()
                    // 处理HTTP状态码
                    .onStatus(HttpStatus::is4xxClientError, response -> {
                        log.error("阿里云API客户端错误, traceId: {}, status: {}", traceId, response.statusCode());
                        return response.bodyToMono(String.class)
                                .map(body -> new WebClientResponseException(
                                        response.statusCode().value(),
                                        response.statusCode().getReasonPhrase(),
                                        response.headers().asHttpHeaders(),
                                        body.getBytes(),
                                        null
                                ));
                    })
                    .onStatus(HttpStatus::is5xxServerError, response -> {
                        log.error("阿里云API服务端错误, traceId: {}, status: {}", traceId, response.statusCode());
                        return response.bodyToMono(String.class)
                                .map(body -> new WebClientResponseException(
                                        response.statusCode().value(),
                                        response.statusCode().getReasonPhrase(),
                                        response.headers().asHttpHeaders(),
                                        body.getBytes(),
                                        null
                                ));
                    })
                    .bodyToMono(String.class)
                    // 阿里云特定的超时设置（覆盖全局配置）
                    .timeout(ALIYUN_RESPONSE_TIMEOUT)
                    .map(response -> {
                        log.debug("阿里云API响应成功, traceId: {}", traceId);
                        return buildSuccessResponse(response, traceId);
                    })
                    .doOnSubscribe(sub ->
                            log.debug("阿里云API请求已发起, traceId: {}", traceId))
                    .doOnError(error -> {
                        if (error instanceof java.util.concurrent.TimeoutException) {
                            log.error("阿里云API调用超时, traceId: {}, timeout: {}s",
                                    traceId, ALIYUN_RESPONSE_TIMEOUT.getSeconds());
                        } else {
                            log.error("阿里云API调用失败, traceId: {}, error: {}", traceId, error.getMessage());
                        }
                    })
                    .doFinally(signalType -> {
                        log.debug("阿里云API调用完成, traceId: {}, signalType: {}", traceId, signalType);
                        TraceIdUtil.clearTraceIdFromMDC();
                    })
                    .contextWrite(ctx -> ctx.put(TraceIdUtil.TRACE_ID, traceId));
        });
    }

    private MessageResponse buildSuccessResponse(String apiResponse, String traceId) {
        MessageResponse response = new MessageResponse();
        response.setSuccess(true);
        response.setMessage("短信发送成功");
        response.setVendor(getVendorName());
        response.setTraceId(traceId);
        return response;
    }

    @Override
    protected String getVendorName() {
        return VENDOR_NAME;
    }

    @Override
    protected String getSecretKey() {
        return SECRET_KEY;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class AliyunSmsRequest {
        private String phoneNumber;
        private String content;
    }
}