package com.example.eaimessage.service;

import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Service;

/**
 * 기존 진입점 유지. 실제 전송은 {@link MessageSendService}에 위임한다.
 */
@Service
public class TalkService {

    private final MessageSendService messageSendService;

    public TalkService(MessageSendService messageSendService) {
        this.messageSendService = messageSendService;
    }

    public void send(TalkRequest request) {
        messageSendService.send(request);
    }
}
