package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.BodyGenerationInput;

public interface MessageBodyGenerator {

    String generate(BodyGenerationInput input);
}
