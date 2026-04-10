package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;

/**
 * 메시지 타입별로 바디에 들어갈 {@link BodyDisplayFields}(templateCode, subject, content)를 만든다.
 * <p>
 * {@code rawContext}는 앞 단계 {@code MessageDataClient#fetch} 결과이며,
 * 여기에 없는 값은 이 리졸버 안에서 쿼리·외부 호출로 보강할 수 있다.
 */
public interface MessageBodyFieldResolver {

    MessageType supportedType();

    BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext);
}
