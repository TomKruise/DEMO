package com.tom.message.sender;

import com.tom.message.model.MessageRequest;
import com.tom.message.model.MessageResponse;
import com.tom.message.template.AbstractMessageSender;
import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
public
class TencentEmailSender extends AbstractMessageSender {

    private static final String VENDOR_NAME = "腾讯云";
    private static final String SECRET_KEY = "tencent-secret-key-789012";

    public TencentEmailSender(WebClient webClient) {
        super(webClient);
    }

    @Override
    protected Mono<MessageResponse> doSend(MessageRequest request) {
        return Mono.deferContextual(contextView -> {
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);

            // 将traceId设置到MDC，确保当前线程的日志能打印traceId
            TraceIdUtil.putTraceIdToMDC(traceId);

            log.info("调用腾讯云邮件API, traceId: {}, recipient: {}", traceId, request.getRecipient());

            return webClient.post()
                    .uri("https://api.tencent.com/email/send")
                    .header("X-TC-Key", getSecretKey())
                    .header("X-Trace-Id", traceId)
                    .bodyValue(new TencentEmailSender.TencentEmailRequest(request.getRecipient(), request.getContent()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        log.debug("腾讯云邮件API响应成功, traceId: {}", traceId);
                        return buildSuccessResponse(response, traceId);
                    })
                    .doOnError(error -> log.error("腾讯云邮件API调用失败, traceId: {}, error: {}", traceId, error.getMessage()))
                    .doFinally(signalType -> {
                        log.debug("腾讯云邮件API调用完成, traceId: {}, signalType: {}", traceId, signalType);
                        TraceIdUtil.clearTraceIdFromMDC();
                    })
                    // 确保traceId在Context中传递
                    .contextWrite(ctx -> ctx.put(TraceIdUtil.TRACE_ID, traceId));
        });
    }

    private Object buildRequestBody(MessageRequest request) {
        return new TencentEmailRequest(request.getRecipient(), request.getContent());
    }

    private MessageResponse buildSuccessResponse(String apiResponse, String traceId) {
        MessageResponse response = new MessageResponse();
        response.setSuccess(true);
        response.setMessage("邮件发送成功: " + apiResponse);
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
    private static class TencentEmailRequest {
        private String email;
        private String content;
    }
}

