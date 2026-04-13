package com.example.eaimessage.service;

import org.springframework.stereotype.Service;

@Service
public class BMessageContentService {

    public String getTitle(String receiverId) {
        return "B_DOCUMENT TITLE";
    }

    public String getContent(String receiverId) {
        return "B_DOCUMENT CONTENT for receiver=" + safe(receiverId);
    }

    private static String safe(String receiverId) {
        return receiverId == null ? "UNKNOWN" : receiverId.trim();
    }
}
