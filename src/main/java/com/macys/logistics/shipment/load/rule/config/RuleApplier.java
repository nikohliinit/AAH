package com.macys.logistics.shipment.load.rule.config;

import com.macys.logistics.shipment.load.dto.dockdoor.RuleContext;

import java.lang.reflect.Method;
import java.util.Map;

public final class RuleApplier {

    private RuleApplier() {}

    public static void apply(Map<String, Object> then, RuleContext ctx) {

        if (then == null || then.isEmpty()) {

            return;

        }

        for (Map.Entry<String, Object> entry : then.entrySet()) {

            String field = entry.getKey();

            Object value = entry.getValue();

            writeField(ctx, field, value);

        }

    }

    private static void writeField(RuleContext ctx, String field, Object value) {

        try {

            String setterName = "set" + capitalize(field);

            // Find setter method

            Method setter = null;

            for (Method m : RuleContext.class.getMethods()) {

                if (m.getName().equals(setterName)) {

                    setter = m;

                    break;

                }

            }

            if (setter == null) {

                throw new IllegalStateException(

                        "No setter found for field '" + field + "'");

            }

            Class<?> paramType = setter.getParameterTypes()[0];

            // 🔥 HANDLE ENUM CONVERSION HERE

            if (paramType.isEnum() && value instanceof String) {

                Object enumValue = Enum.valueOf(

                        (Class<Enum>) paramType,

                        (String) value

                );

                setter.invoke(ctx, enumValue);

            } else {

                setter.invoke(ctx, value);

            }

        } catch (Exception e) {

            throw new IllegalStateException(

                    "Failed to set field '" + field + "' in RuleContext", e);

        }

    }

    private static String capitalize(String val) {

        return val.substring(0, 1).toUpperCase() + val.substring(1);

    }

}

