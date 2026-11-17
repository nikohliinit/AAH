package com.macys.logistics.shipment.load.dto.inbound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ErrorCodeTest {

    @Test
    void testEnumValuesAndGetters() {
        ErrorCode[] values = ErrorCode.values();
        assertTrue(values.length >= 2);
        assertNotNull(ErrorCode.Create_Shipment_Exception);
        assertNotNull(ErrorCode.Delete_Shipment_Exception);

        assertEquals("3001", ErrorCode.Create_Shipment_Exception.getCode());
        assertEquals("Unsuccessful shipment creation in TMS", ErrorCode.Create_Shipment_Exception.getMessage());

        assertEquals("3002", ErrorCode.Delete_Shipment_Exception.getCode());
        assertEquals("Unsuccessful shipment deletion in TMS", ErrorCode.Delete_Shipment_Exception.getMessage());
    }

    @Test
    void testValueOf() {
        assertEquals(ErrorCode.Create_Shipment_Exception, ErrorCode.valueOf("Create_Shipment_Exception"));
        assertEquals(ErrorCode.Delete_Shipment_Exception, ErrorCode.valueOf("Delete_Shipment_Exception"));
    }
}
