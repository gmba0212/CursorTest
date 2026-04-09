package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlContentBuilder extends AbstractMessageContentSupport implements MessageContentBuilder {

    @Override
    public MessageType supportedType() {
        return MessageType.SHORT_URL;
    }

    @Override
    public MessageContent build(TalkRequest request, ServiceData serviceData) {
        return MessageContent.of(
            "SHORT_URL",
            firstNonBlank(request.getTitle(), "[단축URL 안내]"),
            "단축 URL: " + valueOr(serviceData, "shortUrl", "N/A")
        );
    }
}
