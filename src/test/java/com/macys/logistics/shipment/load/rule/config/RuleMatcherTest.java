package com.macys.logistics.shipment.load.rule.config;

import com.macys.logistics.shipment.load.dto.dockdoor.RuleContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuleMatcherTest {


    @Test
    void match_shouldReturnTrue_whenWhenIsNull() {
        RuleContext ctx = new RuleContext();

        assertTrue(RuleMatcher.match(null, ctx));
    }

    @Test
    void match_shouldReturnTrue_whenWhenIsEmpty() {
        RuleContext ctx = new RuleContext();

        assertTrue(RuleMatcher.match(new HashMap<>(), ctx));
    }



    @Test
    void match_shouldThrowException_whenGetterDoesNotExist() {
        RuleContext ctx = new RuleContext();

        Map<String, Object> when = new HashMap<>();
        when.put("unknownField", "value");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> RuleMatcher.match(when, ctx)
        );

        assertEquals(
                "Failed to read field 'unknownField' from RuleContext",
                ex.getMessage()
        );
        assertNotNull(ex.getCause());
    }
}

