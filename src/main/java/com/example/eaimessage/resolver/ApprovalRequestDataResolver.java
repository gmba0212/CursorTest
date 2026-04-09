package com.example.eaimessage.resolver;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import com.example.eaimessage.service.UserInfoService;
import org.springframework.stereotype.Component;

@Component
public class ApprovalRequestDataResolver extends AbstractApprovalDataResolver implements MessageDataResolver {

    public ApprovalRequestDataResolver(OrderInfoService orderInfoService, UserInfoService userInfoService) {
        super(orderInfoService, userInfoService);
    }

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_REQUEST;
    }

    @Override
    public ServiceData resolve(TalkRequest request) {
        return resolveApproval(request);
    }
}
