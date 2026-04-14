package com.example.eaimessage.service;

import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.factory.BodyGeneratorFactory;
import com.example.eaimessage.factory.HeaderGeneratorFactory;
import com.example.eaimessage.generator.body.BodyData;
import com.example.eaimessage.generator.body.DefaultBodyTemplate;
import com.example.eaimessage.generator.header.DefaultHeaderTemplate;
import com.example.eaimessage.generator.header.HeaderData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.TalkRequest;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private static final Logger log = LoggerFactory.getLogger(MessageSendService.class);

    private final HeaderGeneratorFactory headerGeneratorFactory;
    private final BodyGeneratorFactory bodyGeneratorFactory;
    private final DefaultHeaderTemplate defaultHeaderTemplate;
    private final Map<ChannelType, DefaultBodyTemplate> defaultBodyTemplateMap = new EnumMap<>(ChannelType.class);
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;

    public MessageSendService(
        HeaderGeneratorFactory headerGeneratorFactory,
        BodyGeneratorFactory bodyGeneratorFactory,
        DefaultHeaderTemplate defaultHeaderTemplate,
        List<DefaultBodyTemplate> defaultBodyTemplates,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        this.headerGeneratorFactory = headerGeneratorFactory;
        this.bodyGeneratorFactory = bodyGeneratorFactory;
        this.defaultHeaderTemplate = defaultHeaderTemplate;
        for (DefaultBodyTemplate defaultBodyTemplate : defaultBodyTemplates) {
            ChannelType channelType = defaultBodyTemplate.supportChannelType();
            if (defaultBodyTemplateMap.put(channelType, defaultBodyTemplate) != null) {
                log.error("DefaultBodyTemplate 중복 등록 channel={}", channelType);
                throw new IllegalStateException("Duplicate DefaultBodyTemplate for " + channelType);
            }
        }
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
        log.info(
            "MessageSendService 초기화 완료 eaiEndpoint={}, defaultBodyTemplateChannels={}",
            eaiEndpoint,
            defaultBodyTemplateMap.keySet()
        );
    }

    public void send(TalkRequest request) {
        validateRequest(request);
        log.debug(
            "메시지 전송 시작 channel={}, messageType={}, receiverId={}",
            request.getChannelType(),
            request.getMessageType(),
            request.getReceiverId()
        );

        BodyData bodyData = bodyGeneratorFactory.get(request.getChannelType(), request.getMessageType()).generate(request);
        String body = getDefaultBodyTemplate(request.getChannelType()).generate(bodyData);

        HeaderData headerData = headerGeneratorFactory
            .get(request.getChannelType())
            .generate(request, bodyData, utf8Length(body));
        String header = defaultHeaderTemplate.generate(headerData);

        String finalMessage = header + body;
        log.debug(
            "메시지 조립 완료 channel={}, messageType={}, totalUtf8Bytes={}",
            request.getChannelType(),
            request.getMessageType(),
            utf8Length(finalMessage)
        );
        eaiHttpClient.send(new HttpSendRequest(eaiEndpoint, finalMessage));
        log.info(
            "메시지 전송 처리 완료 channel={}, messageType={}, receiverId={}",
            request.getChannelType(),
            request.getMessageType(),
            request.getReceiverId()
        );
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }

    private void validateRequest(TalkRequest request) {
        if (request == null || request.getChannelType() == null) {
            log.warn("요청 유효성 검증 실패: channelType이 null입니다");
            throw new IllegalArgumentException("channelType must not be null");
        }
        if (request.getMessageType() == null) {
            log.warn("요청 유효성 검증 실패: messageType이 null입니다 channel={}", request.getChannelType());
            throw new IllegalArgumentException("messageType must not be null");
        }
        if (request.getReceiverId() == null || request.getReceiverId().isBlank()) {
            log.warn(
                "요청 유효성 검증 실패: receiverId가 비어 있음 channel={}, messageType={}",
                request.getChannelType(),
                request.getMessageType()
            );
            throw new IllegalArgumentException("receiverId must not be blank");
        }
    }

    private DefaultBodyTemplate getDefaultBodyTemplate(ChannelType channelType) {
        DefaultBodyTemplate defaultBodyTemplate = defaultBodyTemplateMap.get(channelType);
        if (defaultBodyTemplate == null) {
            log.error("채널에 대한 DefaultBodyTemplate 없음 channel={}", channelType);
            throw new IllegalArgumentException("No DefaultBodyTemplate for channel " + channelType);
        }
        return defaultBodyTemplate;
    }
}
