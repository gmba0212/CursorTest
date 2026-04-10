package com.example.eaimessage.provider;

import com.example.eaimessage.service.UserInfoService;
import org.springframework.stereotype.Component;

@Component
public class DefaultReceiverInfoResolver {

    private static final String DEFAULT_RECEIVER_TYPE = "USER";

    private final UserInfoService userInfoService;

    public DefaultReceiverInfoResolver(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public String resolveType(String receiverId) {
        return DEFAULT_RECEIVER_TYPE;
    }

    public String resolveDisplayName(String receiverId) {
        return userInfoService.getDisplayName(receiverId);
    }

    public String resolveAddress(String receiverId) {
        return userInfoService.getPhoneNumber(receiverId);
    }
}
