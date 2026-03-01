package com.tom.message.service;

import com.tom.message.factory.MessageSenderFactory;
import com.tom.message.model.MessageRequest;
import com.tom.message.model.MessageResponse;
import com.tom.message.model.MessageType;
import com.tom.message.template.AbstractMessageSender;
import com.tom.message.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSenderFactory senderFactory;

    public Mono<MessageResponse> sendMessage(MessageType messageType, String content, String recipient) {
        MessageRequest request = new MessageRequest(messageType, content, recipient);
        return sendMessage(request);
    }

    public Mono<MessageResponse> sendMessage(MessageRequest request) {
        return Mono.deferContextual(contextView -> {
            // 从Context中获取traceId
            String traceId = TraceIdUtil.getTraceIdFromContext((Context) contextView);

            // 将traceId设置到MDC，确保当前线程的日志能打印traceId
            TraceIdUtil.putTraceIdToMDC(traceId);

            log.info("Service处理消息发送请求, traceId: {}, type: {}, recipient: {}",
                    traceId, request.getMessageType(), request.getRecipient());

            try {
                AbstractMessageSender sender = senderFactory.getSender(request.getMessageType());

                return sender.send(request)
                        .doOnSuccess(response ->
                                log.info("消息发送处理完成, traceId: {}, success: {}", traceId, response.isSuccess()))
                        .doOnError(error ->
                                log.error("消息发送处理失败, traceId: {}, error: {}", traceId, error.getMessage()))
                        .doFinally(signalType -> {
                            log.debug("Service处理完成, traceId: {}, signalType: {}", traceId, signalType);
                            TraceIdUtil.clearTraceIdFromMDC();
                        })
                        .contextWrite(ctx -> ctx.put(TraceIdUtil.TRACE_ID, traceId));

            } catch (Exception e) {
                log.error("获取消息发送器失败, traceId: {}, error: {}", traceId, e.getMessage());
                MessageResponse response = new MessageResponse();
                response.setSuccess(false);
                response.setMessage("获取消息发送器失败: " + e.getMessage());
                response.setTraceId(traceId);
                TraceIdUtil.clearTraceIdFromMDC();
                return Mono.just(response);
            }
        });
    }
}
