package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import com.example.eaimessage.service.UserInfoService;
import org.springframework.stereotype.Component;

@Component
public class ApprovalCompleteDataClient extends AbstractApprovalDataClient implements MessageDataClient {

    public ApprovalCompleteDataClient(OrderInfoService orderInfoService, UserInfoService userInfoService) {
        super(orderInfoService, userInfoService);
    }

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_COMPLETE;
    }

    @Override
    public MessageContext fetch(TalkRequest request) {
        return fetchApproval(request);
    }
}
