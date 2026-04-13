package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.AMessageContentService;
import org.springframework.stereotype.Component;

@Component
public class ADocumentBodyGenerator extends DefaultEaiBodyGenerator {

    private final AMessageContentService messageContentService;

    public ADocumentBodyGenerator(AMessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

    @Override
    public MessageType supportMessageType() {
        return MessageType.A_DOCUMENT;
    }

    @Override
    public BodyGenerationResult generate(TalkRequest request) {
        String title = messageContentService.getTitle(request.getReceiverId());
        String content = messageContentService.getContent(request.getReceiverId());
        return generateWithDefaults(request, title, content);
    }
}
