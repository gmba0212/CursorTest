package com.example.eaimessage.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.eaimessage.model.ChannelType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EaiPropertiesTest {

    @Test
    void resolveEndpoint_usesChannelSpecificUrlWhenPresent() {
        EaiProperties props = new EaiProperties();
        props.setEndpoint("http://default/send");
        props.setChannels(Map.of("A_TALK", "http://atalk/send", "EMAIL", "http://email/send"));

        assertThat(props.resolveEndpoint(ChannelType.A_TALK)).isEqualTo("http://atalk/send");
        assertThat(props.resolveEndpoint(ChannelType.EMAIL)).isEqualTo("http://email/send");
    }

    @Test
    void resolveEndpoint_fallsBackToDefaultEndpoint() {
        EaiProperties props = new EaiProperties();
        props.setEndpoint("http://default/send");
        props.setChannels(Map.of("EMAIL", "http://email/send"));

        assertThat(props.resolveEndpoint(ChannelType.A_TALK)).isEqualTo("http://default/send");
    }
}
