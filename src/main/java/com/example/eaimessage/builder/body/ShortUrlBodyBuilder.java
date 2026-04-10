package com.example.eaimessage.builder.body;

import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlBodyBuilder extends AbstractBodyBuilderSupport implements BodyBuilder {

    private final KTalkProperties kTalkProperties;

    public ShortUrlBodyBuilder(KTalkProperties kTalkProperties) {
        this.kTalkProperties = kTalkProperties;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SHORT_URL;
    }

    @Override
    public String build(TalkRequest request, MessageContext context) {
        return buildByChannel(
            request,
            kTalkProperties,
            "SHORT_URL",
            firstNonBlank(request.getTitle(), "[단축URL 안내]"),
            "단축 URL: " + valueOr(context, "shortUrl", "N/A")
        );
    }
}
