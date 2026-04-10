package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ShortUrlService;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlDataClient implements MessageDataClient {

    private final ShortUrlService shortUrlService;

    public ShortUrlDataClient(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SHORT_URL;
    }

    @Override
    public MessageContext fetch(TalkRequest request) {
        return MessageContext.of(Map.of("shortUrl", shortUrlService.createShortUrl(request.getContent())));
    }
}
