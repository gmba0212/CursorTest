package com.example.eaimessage.config;

import com.example.eaimessage.model.ChannelType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * EAI 전송 URL 설정. {@code eai.endpoint}는 모든 채널의 기본값이며,
 * {@code eai.channels.<ChannelType.name()>}가 있으면 해당 채널에만 적용된다.
 */
@ConfigurationProperties(prefix = "eai")
public class EaiProperties {

    /**
     * 채널별 URL이 없을 때 사용하는 기본 엔드포인트 (기존 {@code eai.endpoint}와 동일 키).
     */
    private String endpoint = "http://localhost:8081/eai/send";

    /**
     * 채널 enum 이름(예: A_TALK, EMAIL)을 키로 하는 전용 URL.
     */
    private Map<String, String> channels = new HashMap<>();

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, String> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, String> channels) {
        this.channels = channels != null ? channels : new HashMap<>();
    }

    public String resolveEndpoint(ChannelType channelType) {
        if (channelType == null) {
            return endpoint;
        }
        String specific = channels.get(channelType.name());
        if (specific != null && !specific.isBlank()) {
            return specific;
        }
        return endpoint != null && !endpoint.isBlank() ? endpoint : "http://localhost:8081/eai/send";
    }
}
