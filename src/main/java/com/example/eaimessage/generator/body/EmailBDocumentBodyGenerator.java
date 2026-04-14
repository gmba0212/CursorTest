package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.BMessageContentService;
import org.springframework.stereotype.Component;

@Component
public class EmailBDocumentBodyGenerator implements EaiBodyGenerator {

    private final BMessageContentService messageContentService;

    public EmailBDocumentBodyGenerator(BMessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public MessageType supportMessageType() {
        return MessageType.B_DOCUMENT;
    }

    @Override
    public BodyData generate(TalkRequest request) {
        String title = messageContentService.getTitle(request.getReceiverId());
        String content = messageContentService.getContent(request.getReceiverId());
        return new BodyData(
            request.getMessageType().name(),
            request.getReceiverId(),
            title,
            content
        );
    }
}
