package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.AMessageContentService;
import com.example.eaimessage.model.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ADocumentBodyGenerator implements EaiBodyGenerator {

    private static final Logger log = LoggerFactory.getLogger(ADocumentBodyGenerator.class);

    private final AMessageContentService messageContentService;

    public ADocumentBodyGenerator(AMessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.A_TALK;
    }

    @Override
    public MessageType supportMessageType() {
        return MessageType.A_DOCUMENT;
    }

    @Override
    public BodyData generate(TalkRequest request) {
        log.debug("A_DOCUMENT 본문 생성 시작 receiverId={}", request.getReceiverId());
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

