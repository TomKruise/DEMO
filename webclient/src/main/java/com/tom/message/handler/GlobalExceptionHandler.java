package com.tom.message.handler;

import com.tom.message.exception.MessageSendException;
import com.tom.message.model.MessageResponse;
import com.tom.message.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理消息发送特定异常
     */
    @ExceptionHandler(MessageSendException.class)
    public Mono<ResponseEntity<MessageResponse>> handleMessageSendException(
            MessageSendException e, ServerWebExchange exchange) {

        String traceId = e.getTraceId() != null ? e.getTraceId() : TraceIdUtil.getTraceId();

        log.error("消息发送异常: vendor={}, traceId={}, errorType={}, message={}",
                e.getVendor(), traceId, e.getErrorType(), e.getMessage(), e);

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage(e.getMessage());
        response.setVendor(e.getVendor());
        response.setTraceId(traceId);

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<MessageResponse>> handleValidationException(
            WebExchangeBindException e, ServerWebExchange exchange) {

        String traceId = TraceIdUtil.getTraceId();

        String errorMessage = e.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("参数验证失败");

        log.error("参数验证异常, traceId: {}, errors: {}", traceId, errorMessage);

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("参数错误: " + errorMessage);
        response.setTraceId(traceId);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<MessageResponse>> handleIllegalArgumentException(
            IllegalArgumentException e, ServerWebExchange exchange) {

        String traceId = TraceIdUtil.getTraceId();

        log.error("非法参数异常, traceId: {}, error: {}", traceId, e.getMessage());

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("参数错误: " + e.getMessage());
        response.setTraceId(traceId);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<MessageResponse>> handleGenericException(
            Exception e, ServerWebExchange exchange) {

        // 从请求中获取traceId
        String traceId = exchange.getAttribute(TraceIdUtil.TRACE_ID);
        if (traceId == null) {
            // 尝试从请求头获取
            traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        }
        if (traceId == null) {
            // 最后从TraceIdUtil获取
            traceId = TraceIdUtil.getTraceId();
        }

        // 设置到MDC以便日志打印
        TraceIdUtil.putTraceIdToMDC(traceId);

        log.error("系统异常, traceId: {}, error: {}", traceId, e.getMessage(), e);

        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage("系统异常: " + e.getMessage());
        response.setTraceId(traceId);

        // 清理MDC
        TraceIdUtil.clearTraceIdFromMDC();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    /**
     * 处理响应式流中的异常
     */
    public static <T> Mono<T> handleReactiveError(Throwable error) {
        String traceId = TraceIdUtil.getTraceIdFromException(error);
        TraceIdUtil.putTraceIdToMDC(traceId);

        log.error("响应式流异常, traceId: {}, error: {}", traceId, error.getMessage(), error);

        TraceIdUtil.clearTraceIdFromMDC();

        if (error instanceof MessageSendException) {
            return Mono.error(error);
        }
        return Mono.error(new MessageSendException("unknown", traceId,
                "系统异常: " + error.getMessage(), MessageSendException.ErrorType.OTHER, error));
    }
}
