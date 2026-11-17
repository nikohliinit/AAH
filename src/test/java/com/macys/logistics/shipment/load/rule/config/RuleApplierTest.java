package com.macys.logistics.shipment.load.rule.config;


import com.macys.logistics.shipment.load.dto.dockdoor.RuleContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuleApplierTest {


    @Test
    void apply_shouldThrowException_whenSetterNotFound() {
        RuleContext ctx = new RuleContext();

        Map<String, Object> then = new HashMap<>();
        then.put("unknownField", "value");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> RuleApplier.apply(then, ctx)
        );

        assertTrue(ex.getMessage().contains("Failed to set field"));
        assertTrue(ex.getCause().getMessage().contains("No setter found"));
    }

    @Test
    void apply_shouldWrapException_whenSetterThrows() {
        RuleContext ctx = new RuleContext() {
            public void setName(String name) {
                throw new RuntimeException("Boom");
            }
        };

        Map<String, Object> then = new HashMap<>();
        then.put("name", "test");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> RuleApplier.apply(then, ctx)
        );

        assertNotNull(ex.getCause());
    }
}

