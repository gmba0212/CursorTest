package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SimpleNoticeContentProvider implements MessageContentProvider {

    private final DefaultReceiverInfoResolver receiverInfoResolver;

    public SimpleNoticeContentProvider(DefaultReceiverInfoResolver receiverInfoResolver) {
        this.receiverInfoResolver = receiverInfoResolver;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SIMPLE_NOTICE;
    }

    @Override
    public PreparedMessageContent provide(TalkRequest request) {
        String receiverId = request.getReceiverId();
        return new PreparedMessageContent(
            "KTALK_NOTICE",
            receiverInfoResolver.resolveType(receiverId),
            receiverInfoResolver.resolveAddress(receiverId),
            receiverId,
            "[안내] 공지 메시지",
            "안내할 공지 사항이 도착했습니다."
        );
    }
}
