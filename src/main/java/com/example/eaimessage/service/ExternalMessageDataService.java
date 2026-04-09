package com.example.eaimessage.service;

import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;

public interface ExternalMessageDataService {
    ServiceData resolve(TalkRequest request);
}
