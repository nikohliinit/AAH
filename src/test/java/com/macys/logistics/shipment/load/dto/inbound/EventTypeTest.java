package com.macys.logistics.shipment.load.dto.inbound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventTypeTest {

    @Test
    void testEnumValues() {
        EventType[] values = EventType.values();
        assertEquals(2, values.length);
        assertEquals(EventType.CREATE, values[0]);
        assertEquals(EventType.DELETE, values[1]);
    }

    @Test
    void testValueOf() {
        assertEquals(EventType.CREATE, EventType.valueOf("CREATE"));
        assertEquals(EventType.DELETE, EventType.valueOf("DELETE"));
    }
}
