package com.example.eaimessage.client;

import com.example.eaimessage.model.HttpSendRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EaiHttpClient {

    private static final Logger log = LoggerFactory.getLogger(EaiHttpClient.class);

    public void send(HttpSendRequest request) {
        int payloadLength = request.getPayload() == null ? 0 : request.getPayload().getBytes(StandardCharsets.UTF_8).length;
        log.debug("EAI HTTP 전송 요청 url={}, payloadUtf8Bytes={}", request.getUrl(), payloadLength);
        log.trace("EAI HTTP 전송 페이로드: {}", request.getPayload());

        HttpPost httpPost = new HttpPost(request.getUrl());
        httpPost.setHeader("Content-Type", "text/plain; charset=UTF-8");
        httpPost.setEntity(new StringEntity(request.getPayload(), StandardCharsets.UTF_8));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                log.error("EAI 전송 실패 url={}, httpStatus={}, payloadUtf8Bytes={}", request.getUrl(), statusCode, payloadLength);
                throw new IllegalStateException("EAI 전송 실패: HTTP " + statusCode);
            }
            log.info("EAI 전송 성공 url={}, httpStatus={}, payloadUtf8Bytes={}", request.getUrl(), statusCode, payloadLength);
        } catch (IOException e) {
            log.error("EAI 전송 IO 오류 url={}, payloadUtf8Bytes={}", request.getUrl(), payloadLength, e);
            throw new IllegalStateException("EAI 전송 중 IO 오류가 발생했습니다.", e);
        }
    }
}
