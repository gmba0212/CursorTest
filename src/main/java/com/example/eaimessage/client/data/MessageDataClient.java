package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;

/**
 * 메시지 타입별 외부 연동/데이터 조회 전담 client.
 */
public interface MessageDataClient {

    MessageType supportedType();

    MessageContext fetch(TalkRequest request);
}
