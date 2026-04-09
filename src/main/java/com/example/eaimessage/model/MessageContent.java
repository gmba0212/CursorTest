package com.example.eaimessage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 채널과 무관한 메시지 본문 스냅샷. {@link com.example.eaimessage.renderer.ChannelMessageRenderer}가
 * 이 값만으로 전송 포맷(ATalk/SMS/Email)을 만든다.
 */
public final class MessageContent {

    private final String templateCode;
    private final String subject;
    private final String bodyText;
    private final Map<String, String> extras;

    public MessageContent(String templateCode, String subject, String bodyText, Map<String, String> extras) {
        this.templateCode = templateCode != null ? templateCode : "";
        this.subject = subject != null ? subject : "";
        this.bodyText = bodyText != null ? bodyText : "";
        this.extras = extras != null ? Map.copyOf(extras) : Map.of();
    }

    public static MessageContent of(String templateCode, String subject, String bodyText) {
        return new MessageContent(templateCode, subject, bodyText, Map.of());
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public Map<String, String> getExtras() {
        return Collections.unmodifiableMap(extras);
    }

    public MessageContent withExtra(String key, String value) {
        Map<String, String> copy = new HashMap<>(extras);
        copy.put(key, value);
        return new MessageContent(templateCode, subject, bodyText, copy);
    }
}
