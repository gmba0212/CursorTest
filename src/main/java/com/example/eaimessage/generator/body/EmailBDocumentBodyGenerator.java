package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.BMessageContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailBDocumentBodyGenerator implements EaiBodyGenerator {

    private static final Logger log = LoggerFactory.getLogger(EmailBDocumentBodyGenerator.class);

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
        log.debug("EMAIL B_DOCUMENT 본문 생성 시작 receiverId={}", request.getReceiverId());
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
