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
}
