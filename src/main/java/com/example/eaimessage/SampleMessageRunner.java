package com.example.eaimessage;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageSendRequest;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.service.MessageSendService;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sample.run", havingValue = "true")
public class SampleMessageRunner implements CommandLineRunner {

    private final MessageSendService messageSendService;

    public SampleMessageRunner(MessageSendService messageSendService) {
        this.messageSendService = messageSendService;
    }

    @Override
    public void run(String... args) {
        MessageSendRequest request = new MessageSendRequest();
        request.setChannelType(ChannelType.ALIMTALK);
        request.setMessageType(MessageType.ATALK_APPROVAL_REQUEST);
        request.setSenderKey("SENDER_KEY_001");
        request.setRecipients(List.of("01012341234"));
        request.setData(Map.of(
            "approverName", "홍길동",
            "documentNo", "DOC-20260406-001"
        ));

        messageSendService.send(request);
    }
}
