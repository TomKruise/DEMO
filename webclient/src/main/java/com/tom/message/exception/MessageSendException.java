package com.tom.message.exception;

public class MessageSendException extends RuntimeException{

    private final String vendor;
    private final String traceId;
    private final ErrorType errorType;

    public MessageSendException(String vendor, String traceId, String message, ErrorType errorType) {
        super(message);
        this.vendor = vendor;
        this.traceId = traceId;
        this.errorType = errorType;
    }

    public MessageSendException(String vendor, String traceId, String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.vendor = vendor;
        this.traceId = traceId;
        this.errorType = errorType;
    }

    public enum ErrorType {
        CONNECTION_TIMEOUT("连接超时"),
        READ_TIMEOUT("读取超时"),
        WRITE_TIMEOUT("写入超时"),
        RESPONSE_TIMEOUT("响应超时"),
        SERVICE_UNAVAILABLE("服务不可用"),
        OTHER("其他错误");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public String getVendor() {
        return vendor;
    }

    public String getTraceId() {
        return traceId;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
