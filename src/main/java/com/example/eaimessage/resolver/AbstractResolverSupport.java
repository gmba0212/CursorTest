package com.example.eaimessage.resolver;

import com.example.eaimessage.model.TalkRequest;

abstract class AbstractResolverSupport {

    protected String param(TalkRequest request, String key) {
        if (request.getParams() == null) {
            return "";
        }
        return stringVal(request.getParams().get(key));
    }

    protected String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
