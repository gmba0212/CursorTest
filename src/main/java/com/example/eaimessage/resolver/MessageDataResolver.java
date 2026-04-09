package com.example.eaimessage.resolver;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;

/**
 * 메시지 타입별 외부 데이터 수집 및 {@link ServiceData} 조립.
 */
public interface MessageDataResolver {

    MessageType supportedType();

    ServiceData resolve(TalkRequest request);
}
