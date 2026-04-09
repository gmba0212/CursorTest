package com.example.eaimessage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eaimessage.ktalk")
public class KTalkProperties {

    /**
     * 알림톡 senderKey (전문 고정필드).
     */
    private String senderKey = "DEFAULT_KTALK_KEY";

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }
}
