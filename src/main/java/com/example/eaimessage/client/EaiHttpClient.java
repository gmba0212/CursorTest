package com.example.eaimessage.client;

import com.example.eaimessage.model.HttpSendRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

@Component
public class EaiHttpClient {

    public void send(HttpSendRequest request) {
        HttpPost httpPost = new HttpPost(request.getUrl());
        httpPost.setHeader("Content-Type", "text/plain; charset=UTF-8");
        httpPost.setEntity(new StringEntity(request.getPayload(), StandardCharsets.UTF_8));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                throw new IllegalStateException("EAI 전송 실패: HTTP " + statusCode);
            }
        } catch (IOException e) {
            throw new IllegalStateException("EAI 전송 중 IO 오류가 발생했습니다.", e);
        }
    }
}
