package com.macys.logistics.shipment.load.rule.config;

import com.macys.logistics.shipment.load.dto.dockdoor.RuleContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
public final class RuleMatcher {

    private RuleMatcher() {
        // utility class
    }
    public static boolean match(Map<String, Object> when, RuleContext ctx) {
        if (when == null || when.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, Object> entry : when.entrySet()) {
            String field = entry.getKey();
            Object expected = entry.getValue();
            Object actual = readField(ctx, field);
            if (!Objects.equals(actual, expected)) {
                return false;
            }
        }
        return true;
    }
    private static Object readField(RuleContext ctx, String field) {
        try {
            String getter = "get" + capitalize(field);
            Method method = RuleContext.class.getMethod(getter);
            return method.invoke(ctx);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read field '" + field + "' from RuleContext", e);
        }
    }
    private static String capitalize(String val) {
        return val.substring(0, 1).toUpperCase() + val.substring(1);
    }
}
