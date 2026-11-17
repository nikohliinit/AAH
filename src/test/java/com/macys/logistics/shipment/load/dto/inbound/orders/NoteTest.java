package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NoteTest {

    @Test
    void testDefaultConstructorAndGettersSetters() {
        Note n = new Note();
        assertNull(n.getFbNoteSeq());
        assertNull(n.getTmsNoteSeq());
        assertNull(n.getText());

        n.setFbNoteSeq(1);
        n.setTmsNoteSeq("TMS1");
        n.setText("Some text");

        assertEquals(1, n.getFbNoteSeq());
        assertEquals("TMS1", n.getTmsNoteSeq());
        assertEquals("Some text", n.getText());
    }

    @Test
    void testEqualsHashCodeToString() {
        Note n1 = new Note();
        n1.setFbNoteSeq(10);
        n1.setTmsNoteSeq("A");
        n1.setText("Hello");

        Note n2 = new Note();
        n2.setFbNoteSeq(10);
        n2.setTmsNoteSeq("A");
        n2.setText("Hello");

        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());

        String ts = n1.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("10"));
        assertTrue(ts.contains("A"));
        assertTrue(ts.contains("Hello"));
    }

    @Test
    void testEqualsEdgeCases() {
        Note n = new Note();
        assertEquals(n, n); // same instance
        assertNotEquals(null, n);
        assertNotEquals("string", n);
    }

    @Test
    void testNotEqualsWhenDifferent() {
        Note n1 = new Note();
        n1.setFbNoteSeq(1);
        Note n2 = new Note();
        n2.setFbNoteSeq(2);
        assertNotEquals(n1, n2);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"fbNoteSeq\":5,\"tmsNoteSeq\":\"SEQ\",\"text\":\"note text\"}";

        ObjectMapper mapper = new ObjectMapper();
        Note deserialized = mapper.readValue(json, Note.class);

        assertEquals(5, deserialized.getFbNoteSeq());
        assertEquals("SEQ", deserialized.getTmsNoteSeq());
        assertEquals("note text", deserialized.getText());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"fbNoteSeq\":5"));
        assertTrue(serialized.contains("\"tmsNoteSeq\":\"SEQ\""));
        assertTrue(serialized.contains("\"text\":\"note text\""));
    }
}
