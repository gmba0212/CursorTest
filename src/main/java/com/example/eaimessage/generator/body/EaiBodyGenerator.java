package com.example.eaimessage.generator.body;

import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.model.TalkRequest;

public interface EaiBodyGenerator {

    String generate(TalkRequest request, MessageContentDto contentDto);
}
