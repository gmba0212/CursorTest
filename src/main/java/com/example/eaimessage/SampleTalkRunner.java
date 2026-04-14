package com.example.eaimessage;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.TalkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sample.run", havingValue = "true")
public class SampleTalkRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleTalkRunner.class);

    private final TalkService talkService;

    public SampleTalkRunner(TalkService talkService) {
        this.talkService = talkService;
    }

    @Override
    public void run(String... args) {
        log.info("샘플 Talk 전송 실행 시작");
        TalkRequest request = TalkRequest.builder()
            .channelType(ChannelType.A_TALK)
            .messageType(MessageType.A_DOCUMENT)
            .receiverId("user-1001")
            .build();

        talkService.send(request);
        log.info("샘플 Talk 전송 실행 완료 channel={}, messageType={}", request.getChannelType(), request.getMessageType());
    }
}
