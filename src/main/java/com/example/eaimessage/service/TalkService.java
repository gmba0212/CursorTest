package com.example.eaimessage.service;

import com.example.eaimessage.model.TalkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 기존 진입점 유지. 실제 전송은 {@link MessageSendService}에 위임한다.
 */
@Service
public class TalkService {

    private static final Logger log = LoggerFactory.getLogger(TalkService.class);

    private final MessageSendService messageSendService;

    public TalkService(MessageSendService messageSendService) {
        this.messageSendService = messageSendService;
    }

    public void send(TalkRequest request) {
        log.debug(
            "TalkService.send 위임 channel={}, messageType={}, receiverId={}",
            request == null ? null : request.getChannelType(),
            request == null ? null : request.getMessageType(),
            request == null ? null : request.getReceiverId()
        );
        messageSendService.send(request);
    }
}
