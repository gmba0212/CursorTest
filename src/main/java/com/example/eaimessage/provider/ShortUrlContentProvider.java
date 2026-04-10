package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ShortUrlService;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlContentProvider implements MessageContentProvider {

    private final ShortUrlService shortUrlService;
    private final DefaultReceiverInfoResolver receiverInfoResolver;

    public ShortUrlContentProvider(
        ShortUrlService shortUrlService,
        DefaultReceiverInfoResolver receiverInfoResolver
    ) {
        this.shortUrlService = shortUrlService;
        this.receiverInfoResolver = receiverInfoResolver;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SHORT_URL;
    }

    @Override
    public PreparedMessageContent provide(TalkRequest request) {
        String receiverId = request.getReceiverId();
        String shortUrl = shortUrlService.createShortUrl("https://example.com/user/" + safe(receiverId));
        return new PreparedMessageContent(
            "SHORT_URL",
            receiverInfoResolver.resolveType(receiverId),
            receiverInfoResolver.resolveAddress(receiverId),
            receiverId,
            "[단축URL 안내]",
            "단축 URL: " + shortUrl
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
