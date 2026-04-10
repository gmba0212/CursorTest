package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlBodyFieldResolver implements MessageBodyFieldResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.SHORT_URL;
    }

    @Override
    public BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext) {
        String shortUrl = BodyFieldResolveSupport.valueOr(rawContext, "shortUrl", "N/A");
        return BodyDisplayFields.of(
            "SHORT_URL",
            BodyFieldResolveSupport.firstNonBlank(request.getTitle(), "[단축URL 안내]"),
            "단축 URL: " + shortUrl
        );
    }
}
