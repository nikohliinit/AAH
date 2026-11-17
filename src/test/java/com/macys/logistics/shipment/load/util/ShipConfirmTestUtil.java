package com.macys.logistics.shipment.load.util;

public class ShipConfirmTestUtil {

    public static String getShipConfirmPayload() {
        return "{\n" +
                "    \"fulfillmentOrder\": {\n" +
                "        \"lineItemList\": {\n" +
                "            \"lineItem\": [\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"itemDepartmentNbr\": 840,\n" +
                "                    \"itemOriginalQuantity\": 2,\n" +
                "                    \"itemPickedQuantity\": 2,\n" +
                "                    \"itemScannedSkuUpc\": 878878669,\n" +
                "                    \"itemSkuUpc\": 878878669,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 1,\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"itemDepartmentNbr\": 840,\n" +
                "                    \"itemOriginalQuantity\": 1,\n" +
                "                    \"itemPickedQuantity\": 1,\n" +
                "                    \"itemScannedSkuUpc\": 233334226,\n" +
                "                    \"itemSkuUpc\": 233334226,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 2,\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"itemDepartmentNbr\": 840,\n" +
                "                    \"itemOriginalQuantity\": 1,\n" +
                "                    \"itemPickedQuantity\": 1,\n" +
                "                    \"itemScannedSkuUpc\": 55634443336,\n" +
                "                    \"itemSkuUpc\": 55634443336,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 3,\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"orderHeader\": {\n" +
                "            \"orderId\": \"VNTDO21680\",\n" +
                "            \"orderID\": \"VNTDO21680\",\n" +
                "            \"sourceApplicationId\": \"MAWM\",\n" +
                "            \"deviceName\": \"MAWM\",\n" +
                "            \"deviceType\": \"Desktop\",\n" +
                "            \"sourceApplicationFunctionType\": \"SHP\",\n" +
                "            \"messageCreateTimestamp\": \"2024-02-02T12:10:21.272Z\",\n" +
                "            \"messageCreateUtcTimestamp\": \"2024-02-02T17:10:21.272Z\",\n" +
                "            \"messageUniversalUniqueId\": \"aef48a6b-e1ee-4b11-ad29-c500a565413f\",\n" +
                "            \"sequenceEpochNbr\": 1706893821272,\n" +
                "            \"eventType\": \"SHPSHPD\",\n" +
                "            \"eventTypeDescription\": \"SHP\",\n" +
                "            \"eventStatus\": \"61\",\n" +
                "            \"eventTimestamp\": \"2024-02-02T12:10:21.272Z\",\n" +
                "            \"eventUtcTimestamp\": \"2024-02-02T17:10:21.272Z\",\n" +
                "            \"orderType\": null,\n" +
                "            \"orderSubType\": null\n" +
                "        },\n" +
                "        \"shipmentList\": {\n" +
                "            \"shipment\": [\n" +
                "                {\n" +
                "                    \"fulfillmentNbr\": \"F04634654\",\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"shipmentState\": \"SHPSHPD\",\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"fulfillmentAssociateId\": \"999999\",\n" +
                "                    \"trailer\": \"\",\n" +
                "                    \"apptNumber\": \"12345\",\n" +
                "                    \"deliveryTrkList\": {\n" +
                "                        \"deliveryTrk\": [\n" +
                "                            {\n" +
                "                                \"cartonSequenceNbr\": 1,\n" +
                "                                \"cartonWeightValue\": 0.9,\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"deliveryCarrierName\": \"UPS Ground\",\n" +
                "                                \"cartonNbr\": \"989898989\",\n" +
                "                                \"freightChargeAmount\": 13.3,\n" +
                "                                \"shipVia\": \"shipViaValue\",\n" +
                "                                \"destLocnNbr\": \"3950\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"cartonContentList\": {\n" +
                "                        \"cartonContent\": [\n" +
                "                            {\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"orderLineItemNbr\": 1,\n" +
                "                                \"itemPackedQuantity\": 2,\n" +
                "                                \"itemSkuUpc\": 878878669,\n" +
                "                                \"itemScannedSkuUpc\": 878878669,\n" +
                "                                \"departmentNbr\": \"\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"orderLineItemNbr\": 2,\n" +
                "                                \"itemPackedQuantity\": 1,\n" +
                "                                \"itemSkuUpc\": 233334226,\n" +
                "                                \"itemScannedSkuUpc\": 233334226,\n" +
                "                                \"departmentNbr\": \"\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fulfillmentNbr\": \"F04634654\",\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"shipmentState\": \"SHPSHPD\",\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"fulfillmentAssociateId\": \"999999\",\n" +
                "                    \"trailer\": \"\",\n" +
                "                    \"apptNumber\": \"12345\",\n" +
                "                    \"deliveryTrkList\": {\n" +
                "                        \"deliveryTrk\": [\n" +
                "                            {\n" +
                "                                \"cartonSequenceNbr\": 2,\n" +
                "                                \"cartonWeightValue\": 0.9,\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"deliveryCarrierName\": \"UPS Ground\",\n" +
                "                                \"cartonNbr\": \"989898989\",\n" +
                "                                \"freightChargeAmount\": 13.3,\n" +
                "                                \"shipVia\": \"shipViaValue\",\n" +
                "                                \"destLocnNbr\": \"3950\"\n" +
                "                            },\n" +
                "\t\t\t\t\t\t\t{\n" +
                "                                \"cartonSequenceNbr\": 3,\n" +
                "                                \"cartonWeightValue\": 0.9,\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"deliveryCarrierName\": \"UPS Ground\",\n" +
                "                                \"cartonNbr\": \"989898989\",\n" +
                "                                \"freightChargeAmount\": 13.3,\n" +
                "                                \"shipVia\": \"shipViaValue\",\n" +
                "                                \"destLocnNbr\": \"3950\"\n" +
                "                            }\n" +
                "\t\t\t\t\t\t\t\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"cartonContentList\": {\n" +
                "                        \"cartonContent\": [\n" +
                "                            {\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"orderLineItemNbr\": 3,\n" +
                "                                \"itemPackedQuantity\": 1,\n" +
                "                                \"itemSkuUpc\": 55634443336,\n" +
                "                                \"itemScannedSkuUpc\": 55634443336,\n" +
                "                                \"departmentNbr\": \"\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public static String getShipConfirmPayloadMissingFields() {
        return "{\n" +
                "    \"fulfillmentOrder\": {\n" +
                "        \"lineItemList\": {\n" +
                "            \"lineItem\": [\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6236,\n" +
                "                    \"itemDepartmentNbr\": 741,\n" +
                "                    \"itemOriginalQuantity\": 15,\n" +
                "                    \"itemPickedQuantity\": 15,\n" +
                "                    \"itemScannedSkuUpc\": 77979588983,\n" +
                "                    \"itemSkuUpc\": 77979588983,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 2,\n" +
                "                    \"orderShipmentNbr\": 5225176,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"orderHeader\": {\n" +
                "            \"orderId\": \"212003171\",\n" +
                "            \"orderID\": \"212003171\",\n" +
                "            \"sourceApplicationId\": \"MAWM\",\n" +
                "            \"deviceName\": \"MAWM\",\n" +
                "            \"deviceType\": \"Desktop\",\n" +
                "            \"sourceApplicationFunctionType\": \"SHP\",\n" +
                "            \"messageCreateTimestamp\": \"2024-02-02T12:10:21.272Z\",\n" +
                "            \"messageCreateUtcTimestamp\": \"2024-02-02T17:10:21.272Z\",\n" +
                "            \"messageUniversalUniqueId\": \"aef48a6b-e1ee-4b11-ad29-c500a565413f\",\n" +
                "            \"sequenceEpochNbr\": 1706893821272,\n" +
                "            \"eventType\": \"SHPSHPD\",\n" +
                "            \"eventTypeDescription\": \"SHP\",\n" +
                "            \"eventStatus\": \"61\",\n" +
                "            \"eventTimestamp\": \"2024-02-02T12:10:21.272Z\",\n" +
                "            \"eventUtcTimestamp\": \"2024-02-02T17:10:21.272Z\",\n" +
                "            \"orderType\": \"Movement\",\n" +
                "            \"orderSubType\": \"HAF\"\n" +
                "        },\n" +
                "        \"shipmentList\": {\n" +
                "            \"shipment\": [\n" +
                "                {\n" +
                "                    \"fulfillmentNbr\": \"5225176\",\n" +
                "                    \"orderShipmentNbr\": 5225176,\n" +
                "                    \"shipmentState\": \"SHPSHPD\",\n" +
                "                    \"fulfillmentLocationNbr\": 6236,\n" +
                "                    \"fulfillmentAssociateId\": \"999999\",\n" +
                "\t\t\t\t\t\"trailer\": \"\",\n" +
                "                    \"apptNumber\": \"12345\",\n" +
                "                    \"deliveryTrkList\": {\n" +
                "                        \"deliveryTrk\": [\n" +
                "                            {\n" +
                "                                \"cartonSequenceNbr\": 1,\n" +
                "                                \"cartonWeightValue\": 0.9,\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090FV1234\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"cartonContentList\": {\n" +
                "                        \"cartonContent\": [\n" +
                "                            {\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090FV1234\",\n" +
                "                                \"orderLineItemNbr\": 2\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public static String getShipConfirmPayloadExtraFields() {
        return "{\n" +
                "    \"fulfillmentOrder\": {\n" +
                "        \"lineItemList\": {\n" +
                "            \"lineItem\": [\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"itemDepartmentNbr\": 840,\n" +
                "                    \"itemOriginalQuantity\": 2,\n" +
                "                    \"itemPickedQuantity\": 2,\n" +
                "                    \"itemScannedSkuUpc\": 878878669,\n" +
                "                    \"itemSkuUpc\": 878878669,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 1,\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"itemDepartmentNbr\": 840,\n" +
                "                    \"itemOriginalQuantity\": 1,\n" +
                "                    \"itemPickedQuantity\": 1,\n" +
                "                    \"itemScannedSkuUpc\": 233334226,\n" +
                "                    \"itemSkuUpc\": 233334226,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 2,\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"itemDepartmentNbr\": 840,\n" +
                "                    \"itemOriginalQuantity\": 1,\n" +
                "                    \"itemPickedQuantity\": 1,\n" +
                "                    \"itemScannedSkuUpc\": 55634443336,\n" +
                "                    \"itemSkuUpc\": 55634443336,\n" +
                "                    \"lineItemState\": \"SHPSHPD\",\n" +
                "                    \"orderLineItemNbr\": 3,\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"itemAdjustedQuantity\": null,\n" +
                "                    \"itemNotInLocationCode\": null,\n" +
                "                    \"lineItemTaskActvty\": {\n" +
                "                        \"associateActivityId\": null,\n" +
                "                        \"deviceName\": \"MAWM\",\n" +
                "                        \"deviceType\": \"Desktop\",\n" +
                "                        \"pickAttemptCount\": null,\n" +
                "                        \"sourceApplicationId\": \"MAWM\",\n" +
                "                        \"fulfillmentAssociateId\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"orderHeader\": {\n" +
                "            \"orderId\": \"VNTDO21680\",\n" +
                "            \"orderID\": \"VNTDO21680\",\n" +
                "            \"sourceApplicationId\": \"MAWM\",\n" +
                "            \"deviceName\": \"MAWM\",\n" +
                "            \"deviceType\": \"Desktop\",\n" +
                "            \"sourceApplicationFunctionType\": \"SHP\",\n" +
                "            \"messageCreateTimestamp\": \"2024-02-02T12:10:21.272Z\",\n" +
                "            \"messageCreateUtcTimestamp\": \"2024-02-02T17:10:21.272Z\",\n" +
                "            \"messageUniversalUniqueId\": \"aef48a6b-e1ee-4b11-ad29-c500a565413f\",\n" +
                "            \"sequenceEpochNbr\": 1706893821272,\n" +
                "            \"eventType\": \"SHPSHPD\",\n" +
                "            \"eventTypeDescription\": \"SHP\",\n" +
                "            \"eventStatus\": \"61\",\n" +
                "            \"eventTimestamp\": \"2024-02-02T12:10:21.272Z\",\n" +
                "            \"eventUtcTimestamp\": \"2024-02-02T17:10:21.272Z\",\n" +
                "            \"orderType\": null,\n" +
                "            \"orderSubType\": null\n" +
                "        },\n" +
                "        \"shipmentList\": {\n" +
                "            \"shipment\": [\n" +
                "                {\n" +
                "                    \"fulfillmentNbr\": \"F04634654\",\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"shipmentState\": \"SHPSHPD\",\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"fulfillmentAssociateId\": \"999999\",\n" +
                "                    \"trailer\": \"\",\n" +
                "                    \"apptNumber\": \"12345\",\n" +
                "                    \"deliveryTrkList\": {\n" +
                "                        \"deliveryTrk\": [\n" +
                "                            {\n" +
                "                                \"cartonSequenceNbr\": 1,\n" +
                "                                \"cartonWeightValue\": 0.9,\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"deliveryCarrierName\": \"UPS Ground\",\n" +
                "                                \"cartonNbr\": \"989898989\",\n" +
                "                                \"freightChargeAmount\": 13.3,\n" +
                "                                \"shipVia\": \"shipViaValue\",\n" +
                "                                \"destLocnNbr\": \"3950\",\n" +
                "\t\t\t\t\t\t\t\t\"testDelivery\": \"test_123_delivery\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"cartonContentList\": {\n" +
                "                        \"cartonContent\": [\n" +
                "                            {\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"orderLineItemNbr\": 1,\n" +
                "                                \"itemPackedQuantity\": 2,\n" +
                "                                \"itemSkuUpc\": 878878669,\n" +
                "                                \"itemScannedSkuUpc\": 878878669,\n" +
                "                                \"departmentNbr\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"testCarton\": \"test_123_carton\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fulfillmentNbr\": \"F04634654\",\n" +
                "                    \"orderShipmentNbr\": 1,\n" +
                "                    \"shipmentState\": \"SHPSHPD\",\n" +
                "                    \"fulfillmentLocationNbr\": 6230,\n" +
                "                    \"fulfillmentAssociateId\": \"999999\",\n" +
                "                    \"trailer\": \"\",\n" +
                "                    \"apptNumber\": \"12345\",\n" +
                "                    \"deliveryTrkList\": {\n" +
                "                        \"deliveryTrk\": [\n" +
                "                            {\n" +
                "                                \"cartonSequenceNbr\": 2,\n" +
                "                                \"cartonWeightValue\": 0.9,\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"deliveryCarrierName\": \"UPS Ground\",\n" +
                "                                \"cartonNbr\": \"989898989\",\n" +
                "                                \"freightChargeAmount\": 13.3,\n" +
                "                                \"shipVia\": \"shipViaValue\",\n" +
                "                                \"destLocnNbr\": \"3950\",\n" +
                "\t\t\t\t\t\t\t\t\"testDelivery\": \"test_456_delivery\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"cartonContentList\": {\n" +
                "                        \"cartonContent\": [\n" +
                "                            {\n" +
                "                                \"deliveryTrackingNbr\": \"1Z9090909090\",\n" +
                "                                \"orderLineItemNbr\": 3,\n" +
                "                                \"itemPackedQuantity\": 1,\n" +
                "                                \"itemSkuUpc\": 55634443336,\n" +
                "                                \"itemScannedSkuUpc\": 55634443336,\n" +
                "                                \"departmentNbr\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"testCarton\": \"test_456_carton\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }
}
