package com.example.eaimessage.resolver;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ShortUrlService;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlDataResolver implements MessageDataResolver {

    private final ShortUrlService shortUrlService;

    public ShortUrlDataResolver(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SHORT_URL;
    }

    @Override
    public ServiceData resolve(TalkRequest request) {
        String longUrl = request.getContent();
        return new ServiceData(Map.of("shortUrl", shortUrlService.createShortUrl(longUrl)));
    }
}
