package com.example.eaimessage.service;

import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.factory.BodyGeneratorFactory;
import com.example.eaimessage.factory.HeaderGeneratorFactory;
import com.example.eaimessage.generator.body.BodyData;
import com.example.eaimessage.generator.body.DefaultBodyTemplate;
import com.example.eaimessage.generator.header.DefaultHeaderTemplate;
import com.example.eaimessage.generator.header.HeaderData;
import com.example.eaimessage.config.EaiProperties;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.TalkRequest;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;

@Service
public class MessageSendService {

    private final HeaderGeneratorFactory headerGeneratorFactory;
    private final BodyGeneratorFactory bodyGeneratorFactory;
    private final DefaultHeaderTemplate defaultHeaderTemplate;
    private final Map<ChannelType, DefaultBodyTemplate> defaultBodyTemplateMap = new EnumMap<>(ChannelType.class);
    private final EaiHttpClient eaiHttpClient;
    private final EaiProperties eaiProperties;

    public MessageSendService(
        HeaderGeneratorFactory headerGeneratorFactory,
        BodyGeneratorFactory bodyGeneratorFactory,
        DefaultHeaderTemplate defaultHeaderTemplate,
        List<DefaultBodyTemplate> defaultBodyTemplates,
        EaiHttpClient eaiHttpClient,
        @Nullable EaiProperties eaiProperties
    ) {
        this.headerGeneratorFactory = headerGeneratorFactory;
        this.bodyGeneratorFactory = bodyGeneratorFactory;
        this.defaultHeaderTemplate = defaultHeaderTemplate;
        for (DefaultBodyTemplate defaultBodyTemplate : defaultBodyTemplates) {
            ChannelType channelType = defaultBodyTemplate.supportChannelType();
            if (defaultBodyTemplateMap.put(channelType, defaultBodyTemplate) != null) {
                throw new IllegalStateException("Duplicate DefaultBodyTemplate for " + channelType);
            }
        }
        this.eaiHttpClient = eaiHttpClient;
        this.eaiProperties = eaiProperties != null ? eaiProperties : new EaiProperties();
    }

    public void send(TalkRequest request) {
        validateRequest(request);

        BodyData bodyData = bodyGeneratorFactory.get(request.getChannelType(), request.getMessageType()).generate(request);
        String body = getDefaultBodyTemplate(request.getChannelType()).generate(bodyData);

        HeaderData headerData = headerGeneratorFactory
            .get(request.getChannelType())
            .generate(request, bodyData, utf8Length(body));
        String header = defaultHeaderTemplate.generate(headerData);

        String finalMessage = header + body;
        String url = eaiProperties.resolveEndpoint(request.getChannelType());
        eaiHttpClient.send(new HttpSendRequest(url, finalMessage));
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }

    private static void validateRequest(TalkRequest request) {
        if (request == null || request.getChannelType() == null) {
            throw new IllegalArgumentException("channelType must not be null");
        }
        if (request.getMessageType() == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        if (request.getReceiverId() == null || request.getReceiverId().isBlank()) {
            throw new IllegalArgumentException("receiverId must not be blank");
        }
    }

    private DefaultBodyTemplate getDefaultBodyTemplate(ChannelType channelType) {
        DefaultBodyTemplate defaultBodyTemplate = defaultBodyTemplateMap.get(channelType);
        if (defaultBodyTemplate == null) {
            throw new IllegalArgumentException("No DefaultBodyTemplate for channel " + channelType);
        }
        return defaultBodyTemplate;
    }
}
