package com.example.eaimessage.service;

import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    public String getUserInfo(String userId) {
        if (userId == null || userId.isBlank()) {
            return "미지정사용자";
        }
        return userId + "-사용자";
    }

    /**
     * 승인자 표시명 등. 요청에 전용 필드가 없을 때 수신자 식별자 기반으로 조회한다.
     */
    public String getDisplayName(String receiverId) {
        return getUserInfo(receiverId);
    }

    public String getUserPhone(String receiverId) {
        if (receiverId == null || receiverId.isBlank()) {
            return "01000000000";
        }
        String normalized = receiverId.replaceAll("[^0-9]", "");
        if (normalized.length() >= 11) {
            return normalized.substring(0, 11);
        }
        return "010" + String.format("%08d", Math.abs(receiverId.hashCode()) % 100000000);
    }
}
