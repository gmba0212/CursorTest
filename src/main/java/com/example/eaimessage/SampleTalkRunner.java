package com.example.eaimessage;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.TalkService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sample.run", havingValue = "true")
public class SampleTalkRunner implements CommandLineRunner {

    private final TalkService talkService;

    public SampleTalkRunner(TalkService talkService) {
        this.talkService = talkService;
    }

    @Override
    public void run(String... args) {
        TalkRequest request = TalkRequest.builder()
            .channelType(ChannelType.KTALK)
            .messageType(MessageType.SHORT_URL)
            .title("[단축URL] 안내")
            .receiverType("USER")
            .receiverAddress("01012341234")
            .receiverId("user-1001")
            .content("https://example.com/orders/ORD-20260406-1001")
            .build();

        talkService.send(request);
    }
}
