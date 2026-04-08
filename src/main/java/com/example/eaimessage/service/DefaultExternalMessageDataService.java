package com.example.eaimessage.service;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DefaultExternalMessageDataService implements ExternalMessageDataService {

    private final OrderInfoService orderInfoService;
    private final UserInfoService userInfoService;
    private final AuthService authService;
    private final ShortUrlService shortUrlService;

    public DefaultExternalMessageDataService(
        OrderInfoService orderInfoService,
        UserInfoService userInfoService,
        AuthService authService,
        ShortUrlService shortUrlService
    ) {
        this.orderInfoService = orderInfoService;
        this.userInfoService = userInfoService;
        this.authService = authService;
        this.shortUrlService = shortUrlService;
    }

    @Override
    public ServiceData resolve(TalkRequest request) {
        MessageType type = request.getMessageType();
        Map<String, Object> map = new HashMap<>();

        String orderNo = param(request, "orderNo");
        String userId = request.getReceiverId() == null ? "" : request.getReceiverId();

        switch (type) {
            case APPROVAL_REQUEST:
            case APPROVAL_COMPLETE:
                map.put("orderInfo", orderInfoService.getOrderInfo(orderNo));
                map.put("userInfo", userInfoService.getUserInfo(userId));
                map.put("documentNo", orderNo.isBlank() ? "DOC-UNKNOWN" : orderNo);
                map.put("approverName", request.getParams() == null
                    ? ""
                    : stringVal(request.getParams().get("approverName")));
                map.put("approveDate", LocalDate.now().toString());
                break;
            case SHORT_URL:
                map.put("shortUrl", shortUrlService.createShortUrl(param(request, "url")));
                break;
            case AUTH_CODE:
                map.put("authCode", authService.getAuthCode());
                break;
            case SIMPLE_NOTICE:
                map.put("notice", "simple");
                break;
            case SETTLEMENT_RESULT:
                map.put("settlementStatus", orderInfoService.getSettlementResult(orderNo));
                map.put("orderNo", orderNo);
                break;
            default:
                throw new IllegalArgumentException("unsupported messageType: " + type);
        }
        return new ServiceData(map);
    }

    private String param(TalkRequest request, String key) {
        if (request.getParams() == null) {
            return "";
        }
        return stringVal(request.getParams().get(key));
    }

    private String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
