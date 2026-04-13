package com.example.eaimessage.service;

import org.springframework.stereotype.Service;

@Service
public class AMessageContentService {

    public String getTitle(String receiverId) {
        return "A_DOCUMENT TITLE";
    }

    public String getContent(String receiverId) {
        return "A_DOCUMENT CONTENT for receiver=" + safe(receiverId);
    }

    private static String safe(String receiverId) {
        return receiverId == null ? "UNKNOWN" : receiverId.trim();
    }
}
