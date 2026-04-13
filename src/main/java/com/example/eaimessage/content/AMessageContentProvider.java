package com.example.eaimessage.content;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.AMessageContentService;
import org.springframework.stereotype.Component;

@Component
public class AMessageContentProvider implements MessageContentProvider {

    private final AMessageContentService messageContentService;

    public AMessageContentProvider(AMessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

    @Override
    public MessageType supportType() {
        return MessageType.A_MESSAGE;
    }

    @Override
    public MessageContentDto getContent(TalkRequest request) {
        return new MessageContentDto(
            messageContentService.getTitle(request),
            messageContentService.getContent(request)
        );
    }
}
