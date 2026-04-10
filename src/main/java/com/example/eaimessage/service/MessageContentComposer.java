package com.example.eaimessage.service;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.TalkRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageContentComposer {

    public MessageContext compose(TalkRequest request, MessageContext rawContext) {
        Map<String, Object> map = new HashMap<>(rawContext.data());

        switch (request.getMessageType()) {
            case SIMPLE_NOTICE -> {
                map.put("templateCode", "KTALK_NOTICE");
                map.put("subject", defaultString(request.getTitle()));
                map.put("content", defaultString(request.getContent()));
            }
            case SHORT_URL -> {
                map.put("templateCode", "SHORT_URL");
                map.put("subject", firstNonBlank(request.getTitle(), "[단축URL 안내]"));
                map.put("content", "단축 URL: " + valueOr(rawContext, "shortUrl", "N/A"));
            }
            case AUTH_CODE -> {
                map.put("templateCode", "AUTH_CODE");
                map.put("subject", "[인증번호]");
                map.put("content", "인증번호는 [" + valueOr(rawContext, "authCode", "000000") + "] 입니다.");
            }
            case APPROVAL_REQUEST -> {
                map.put("templateCode", "APRV_REQ");
                map.put("subject", "[승인요청] " + valueOr(rawContext, "approverName", "담당자"));
                map.put("content", "결재 문서번호: " + valueOr(rawContext, "documentNo", "N/A"));
            }
            case APPROVAL_COMPLETE -> {
                map.put("templateCode", "APRV_DONE");
                map.put("subject", "[승인완료] " + valueOr(rawContext, "approverName", "담당자"));
                map.put("content", "결재 완료 (" + valueOr(rawContext, "approveDate", "N/A") + ")");
            }
            case SETTLEMENT_RESULT -> {
                map.put("templateCode", "SETTLE_RES");
                map.put("subject", "[정산결과] " + valueOr(rawContext, "orderNo", "N/A"));
                map.put("content", "정산 결과: " + valueOr(rawContext, "settlementStatus", "UNKNOWN"));
            }
            default -> throw new IllegalArgumentException("Unsupported messageType: " + request.getMessageType());
        }

        applyChannelDefaults(request, map);
        return MessageContext.of(map);
    }

    private void applyChannelDefaults(TalkRequest request, Map<String, Object> map) {
        if (request.getChannelType() == null) {
            return;
        }
        switch (request.getChannelType()) {
            case EMAIL -> {
                map.put("subject", firstNonBlank((String) map.get("subject"), request.getTitle(), "[메일]"));
                map.put("receiverType", firstNonBlank(request.getReceiverType(), "USER"));
            }
            case SMS -> {
                map.put("subject", firstNonBlank((String) map.get("subject"), request.getTitle(), "알림"));
                map.put("content", firstNonBlank((String) map.get("content"), request.getContent(), "SMS 안내 메시지입니다."));
            }
            default -> {
            }
        }
    }

    private String valueOr(MessageContext data, String key, String defaultValue) {
        String v = data.getString(key);
        return v == null || v.isBlank() ? defaultValue : v;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return "";
    }
}
