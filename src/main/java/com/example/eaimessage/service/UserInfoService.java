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
}
