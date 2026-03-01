package com.tom.message.model;

public enum MessageType {
    SMS("sms", "阿里云"),
    EMAIL("email", "腾讯云"),
    STATION_LETTER("station_letter", "华为云");

    private String type;
    private String defaultVendor;

    MessageType(String type, String defaultVendor) {
        this.type = type;
        this.defaultVendor = defaultVendor;
    }

    public String getType() {
        return type;
    }

    public String getDefaultVendor() {
        return defaultVendor;
    }

    public static MessageType fromType(String type) {
        for (MessageType messageType : values()) {
            if (messageType.getType().equals(type)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + type);
    }
}
