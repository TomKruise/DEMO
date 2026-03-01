package com.tom.message.controller;

import com.tom.message.model.MessageRequest;
import com.tom.message.model.MessageResponse;
import com.tom.message.model.MessageType;
import com.tom.message.service.MessageService;
import com.tom.message.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Mono<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        return Mono.deferContextual(contextView -> {
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);
            log.info("Controller收到消息发送请求, traceId: {}, request: {}", traceId, request);

            return messageService.sendMessage(request)
                    .doOnSuccess(response ->
                            log.info("Controller返回响应, traceId: {}, success: {}", traceId, response.isSuccess()))
                    .contextWrite(ctx -> ctx);
        });
    }

    @PostMapping("/send/{type}")
    public Mono<MessageResponse> sendMessage(
            @PathVariable String type,
            @RequestParam String recipient,
            @RequestParam String content) {

        return Mono.deferContextual(contextView -> {
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);
            log.info("Controller收到消息发送请求, traceId: {}, type: {}", traceId, type);

            try {
                MessageType messageType = MessageType.fromType(type);
                return messageService.sendMessage(messageType, content, recipient)
                        .doOnSuccess(response ->
                                log.info("Controller返回响应, traceId: {}, success: {}", traceId, response.isSuccess()))
                        .contextWrite(ctx -> ctx);

            } catch (IllegalArgumentException e) {
                log.error("不支持的消息类型: {}, traceId: {}", type, traceId);
                MessageResponse response = new MessageResponse();
                response.setSuccess(false);
                response.setMessage("不支持的消息类型: " + type);
                response.setTraceId(traceId);
                return Mono.just(response);
            }
        });
    }
}
