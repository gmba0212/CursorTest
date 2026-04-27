package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.MgCardUsageReportContentService;
import org.springframework.stereotype.Component;

@Component
public class EmailMgCardUsageReportBodyGenerator implements EaiBodyGenerator {

    private final MgCardUsageReportContentService contentService;

    public EmailMgCardUsageReportBodyGenerator(MgCardUsageReportContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public MessageType supportMessageType() {
        return MessageType.MGCARD_USAGE_REPORT;
    }

    @Override
    public BodyData generate(TalkRequest request) {
        return new BodyData(
            request.getMessageType().name(),
            request.getReceiverId(),
            contentService.getTitle(request.getReceiverId()),
            contentService.getContent(request.getReceiverId())
        );
    }
}
