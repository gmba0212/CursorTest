package com.example.eaimessage.service;

import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Service;

@Service
public class AMessageContentService {

    public String getTitle(TalkRequest request) {
        return "A_MESSAGE TITLE";
    }

    public String getContent(TalkRequest request) {
        return "A_MESSAGE CONTENT for receiver=" + safe(request.getReceiverId());
    }

    private static String safe(String receiverId) {
        return receiverId == null ? "UNKNOWN" : receiverId.trim();
    }
}
