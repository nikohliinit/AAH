package com.macys.logistics.shipment.load.dto.inbound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DivisionTest {

    @Test
    void testEnumValuesAndGetters() {
        // Validate constants exist
        Division[] values = Division.values();
        assertTrue(values.length >= 4);
        assertNotNull(Division.MCY);
        assertNotNull(Division.BOS);
        assertNotNull(Division.BLM);
        assertNotNull(Division.MBK);

        // Validate getters for a couple of enums
        assertEquals(71, Division.MCY.getDivNumber());
        assertEquals("MCY", Division.MCY.getAbbreviation());

        assertEquals(76, Division.BOS.getDivNumber());
        assertEquals("BOS", Division.BOS.getAbbreviation());
    }

    @Test
    void testValueOf() {
        assertEquals(Division.MCY, Division.valueOf("MCY"));
        assertEquals(Division.BOS, Division.valueOf("BOS"));
        assertEquals(Division.BLM, Division.valueOf("BLM"));
        assertEquals(Division.MBK, Division.valueOf("MBK"));
    }

    @Test
    void testGetDivAbbreviationByDivNumberSuccess() {
        assertEquals("MCY", Division.getDivAbbreviationByDivNumber(71));
        assertEquals("BOS", Division.getDivAbbreviationByDivNumber(76));
        assertEquals("BLM", Division.getDivAbbreviationByDivNumber(72));
        assertEquals("MBK", Division.getDivAbbreviationByDivNumber(77));
    }

    @Test
    void testGetDivAbbreviationByDivNumberInvalid() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Division.getDivAbbreviationByDivNumber(999));
        assertTrue(ex.getMessage().contains("Invalid division number: 999"));
    }
}
