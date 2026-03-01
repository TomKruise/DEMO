package com.tom.message.util;

import com.tom.message.exception.MessageSendException;
import org.slf4j.MDC;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class TraceIdUtil {
    public static final String TRACE_ID = "traceId";

    // 使用ThreadLocal存储traceId，但在WebFlux中需要注意
    private static final ThreadLocal<String> currentTraceId = new ThreadLocal<>();

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置traceId到ThreadLocal和MDC
     */
    public static void setTraceId(String traceId) {
        if (traceId != null) {
            currentTraceId.set(traceId);
            MDC.put(TRACE_ID, traceId);
        }
    }

    /**
     * 获取当前线程的traceId
     * 如果ThreadLocal中没有，尝试从MDC获取
     * 如果都没有，生成新的
     */
    public static String getTraceId() {
        // 先从ThreadLocal获取
        String traceId = currentTraceId.get();
        if (traceId != null && !traceId.isEmpty()) {
            return traceId;
        }

        // 再从MDC获取
        traceId = MDC.get(TRACE_ID);
        if (traceId != null && !traceId.isEmpty()) {
            currentTraceId.set(traceId);
            return traceId;
        }

        // 最后生成新的
        traceId = generateTraceId();
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 清除traceId
     */
    public static void clearTraceId() {
        currentTraceId.remove();
        MDC.remove(TRACE_ID);
    }

    /**
     * 将traceId设置到MDC中（用于日志输出）
     */
    public static void putTraceIdToMDC(String traceId) {
        if (traceId != null) {
            MDC.put(TRACE_ID, traceId);
            currentTraceId.set(traceId);
        }
    }

    /**
     * 从MDC中清除traceId
     */
    public static void clearTraceIdFromMDC() {
        MDC.remove(TRACE_ID);
        // 注意：这里不清理ThreadLocal，因为可能在同一个线程中还有后续操作
    }

    /**
     * 用于在WebFlux操作中设置MDC的consumer
     * 在每个信号（onNext/onError/onComplete）执行前，将Context中的traceId设置到MDC
     */
    public static <T> Consumer<Signal<T>> mdcContext() {
        return signal -> {
            if (!signal.isOnSubscribe()) {
                String traceId = signal.getContextView().getOrDefault(TRACE_ID, "");
                if (!traceId.isEmpty()) {
                    MDC.put(TRACE_ID, traceId);
                    currentTraceId.set(traceId);
                }
            }
        };
    }

    /**
     * 创建包含traceId的Context
     */
    public static Context putTraceIdInContext(Context context, String traceId) {
        return context.put(TRACE_ID, traceId);
    }

    /**
     * 从Context中获取traceId
     */
    public static String getTraceIdFromContext(Context context) {
        return context.getOrDefault(TRACE_ID, null);
    }

    /**
     * 确保traceId在Context中传递
     */
    public static Context ensureTraceIdInContext(Context context) {
        if (!context.hasKey(TRACE_ID)) {
            String traceId = getTraceId();
            return context.put(TRACE_ID, traceId);
        }
        return context;
    }

    /**
     * 从异常中尝试获取traceId
     * 用于全局异常处理器
     */
    public static String getTraceIdFromException(Throwable exception) {
        // 尝试从异常中查找traceId
        if (exception instanceof MessageSendException) {
            return ((MessageSendException) exception).getTraceId();
        }

        // 其他情况返回当前线程的traceId
        return getTraceId();
    }
}
