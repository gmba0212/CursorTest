package com.example.eaimessage.service;

import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Service;

@Service
public class BMessageContentService {

    public String getTitle(TalkRequest request) {
        return "B_MESSAGE TITLE";
    }

    public String getContent(TalkRequest request) {
        return "B_MESSAGE CONTENT for receiver=" + safe(request.getReceiverId());
    }

    private static String safe(String receiverId) {
        return receiverId == null ? "UNKNOWN" : receiverId.trim();
    }
}
