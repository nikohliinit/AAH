package com.macys.logistics.shipment.load.dto.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrdersTest {

    @Test
    void testGettersSettersAndDefaults() {
        PurchaseOrders po = new PurchaseOrders();
        assertNull(po.getPoNumber());
        assertEquals(0, po.getDivisionNumber());
        assertEquals(0, po.getDepartmentNumber());
        assertNull(po.getInDcDate());
        assertNull(po.getCancelDate());
        assertNull(po.getPoStatus());

        po.setPoNumber(123456789L);
        po.setDivisionNumber(71);
        po.setDepartmentNumber(845);
        po.setInDcDate("2026-03-01");
        po.setCancelDate("2026-03-10");
        po.setPoStatus("OPEN");

        assertEquals(123456789L, po.getPoNumber());
        assertEquals(71, po.getDivisionNumber());
        assertEquals(845, po.getDepartmentNumber());
        assertEquals("2026-03-01", po.getInDcDate());
        assertEquals("2026-03-10", po.getCancelDate());
        assertEquals("OPEN", po.getPoStatus());
    }

    @Test
    void testEqualsHashCodeToString() {
        PurchaseOrders a = new PurchaseOrders();
        a.setPoNumber(1L); a.setDivisionNumber(71); a.setDepartmentNumber(100);
        a.setInDcDate("D1"); a.setCancelDate("C1"); a.setPoStatus("S1");

        PurchaseOrders b = new PurchaseOrders();
        b.setPoNumber(1L); b.setDivisionNumber(71); b.setDepartmentNumber(100);
        b.setInDcDate("D1"); b.setCancelDate("C1"); b.setPoStatus("S1");

        PurchaseOrders c = new PurchaseOrders();
        c.setPoNumber(2L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertNotNull(ts);
        assertTrue(ts.contains("poNumber=1"));
        assertTrue(ts.contains("divisionNumber=71"));
    }

    @Test
    void testEqualsEdgeCases() {
        PurchaseOrders po = new PurchaseOrders();
        assertEquals(po, po);
        assertNotEquals(null, po);
        assertNotEquals("string", po);
    }

    @Test
    void testJacksonSerializationDeserialization() throws JsonProcessingException {
        String json = "{\"poNumber\":999,\"divisionNumber\":72,\"departmentNumber\":200,\"inDcDate\":\"2026-01-02\",\"cancelDate\":\"2026-01-10\",\"poStatus\":\"CLOSED\"}";

        ObjectMapper mapper = new ObjectMapper();
        PurchaseOrders deserialized = mapper.readValue(json, PurchaseOrders.class);

        assertEquals(999L, deserialized.getPoNumber());
        assertEquals(72, deserialized.getDivisionNumber());
        assertEquals(200, deserialized.getDepartmentNumber());
        assertEquals("2026-01-02", deserialized.getInDcDate());
        assertEquals("2026-01-10", deserialized.getCancelDate());
        assertEquals("CLOSED", deserialized.getPoStatus());

        String serialized = mapper.writeValueAsString(deserialized);
        assertTrue(serialized.contains("\"poNumber\":999"));
        assertTrue(serialized.contains("\"divisionNumber\":72"));
        assertTrue(serialized.contains("\"poStatus\":\"CLOSED\""));
    }
}
