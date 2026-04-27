package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.CreateCardUsageReportService;
import org.springframework.stereotype.Component;

@Component
public class EmailMgCardUsageReportBodyGenerator implements EaiBodyGenerator {

    private final CreateCardUsageReportService createCardUsageReportService;

    public EmailMgCardUsageReportBodyGenerator(CreateCardUsageReportService createCardUsageReportService) {
        this.createCardUsageReportService = createCardUsageReportService;
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
            createCardUsageReportService.createTitle(),
            createCardUsageReportService.createContent(request)
        );
    }
}
