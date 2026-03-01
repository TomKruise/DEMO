package com.tom.message.factory;

import com.tom.message.model.MessageType;
import com.tom.message.sender.AliyunSmsSender;
import com.tom.message.sender.HuaweiStationLetterSender;
import com.tom.message.sender.TencentEmailSender;
import com.tom.message.template.AbstractMessageSender;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.EnumMap;
import java.util.Map;

@Component
public class MessageSenderFactory {
    private final Map<MessageType, AbstractMessageSender> senderMap = new EnumMap<>(MessageType.class);

    public MessageSenderFactory(WebClient webClient) {
        // 初始化所有sender
        senderMap.put(MessageType.SMS, new AliyunSmsSender(webClient));
        senderMap.put(MessageType.EMAIL, new TencentEmailSender(webClient));
        senderMap.put(MessageType.STATION_LETTER, new HuaweiStationLetterSender(webClient));
    }

    public AbstractMessageSender getSender(MessageType messageType) {
        AbstractMessageSender sender = senderMap.get(messageType);
        if (sender == null) {
            throw new IllegalArgumentException("不支持的消息类型: " + messageType);
        }
        return sender;
    }
}
