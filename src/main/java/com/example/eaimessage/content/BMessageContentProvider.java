package com.example.eaimessage.content;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.BMessageContentService;
import org.springframework.stereotype.Component;

@Component
public class BMessageContentProvider implements MessageContentProvider {

    private final BMessageContentService messageContentService;

    public BMessageContentProvider(BMessageContentService messageContentService) {
        this.messageContentService = messageContentService;
    }

    @Override
    public MessageType supportType() {
        return MessageType.B_MESSAGE;
    }

    @Override
    public MessageContentDto getContent(TalkRequest request) {
        return new MessageContentDto(
            messageContentService.getTitle(request),
            messageContentService.getContent(request)
        );
    }
}
