package com.example.eaimessage.header;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EaiHeaderGeneratorTest {

    private static final int TOTAL_LENGTH = 89;

    @Test
    void create_shouldKeepFixedLengthAndTruncateOverflowFields() {
        EaiHeaderGenerator generator = new EaiHeaderGenerator();

        String header = generator.create(
            "20260409112233445566778899",
            "KTALK-CHANNEL-OVERFLOW",
            "APPROVAL_REQUEST_MESSAGE_TYPE_OVERFLOW",
            123
        );

        assertEquals(TOTAL_LENGTH, header.length());
        assertEquals("20260409112233445566", header.substring(21, 41));
        assertEquals("KTALK-CHAN", header.substring(41, 51));
        assertEquals("APPROVAL_REQUEST_MESSAGE_TYPE_O", header.substring(51, 81));
        assertEquals("00000123", header.substring(81, 89));
    }

    @Test
    void create_shouldZeroPadAndClampNegativeBodyLength() {
        EaiHeaderGenerator generator = new EaiHeaderGenerator();

        String header = generator.create("TX-1", "SMS", "AUTH_CODE", -1);

        assertEquals(TOTAL_LENGTH, header.length());
        assertEquals("00000000", header.substring(81, 89));
    }
}
