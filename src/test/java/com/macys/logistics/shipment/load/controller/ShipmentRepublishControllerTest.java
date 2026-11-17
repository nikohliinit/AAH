package com.macys.logistics.shipment.load.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.inbound.ShipmentEnterpriseMessage;
import com.macys.logistics.shipment.load.service.InBoundShipmentService;
import com.macys.logistics.shipment.load.util.CommonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShipmentRepublishController.class, properties = {
    "create.shipment.load.controller.enable=true",
    "tms.service.connectionTimeOut=5000",
    "tms.service.readTimeOut=5000","dockdoor.retry.maxAttempts=3",
        "dockdoor.retry.backOffPeriod=2000"
})
class ShipmentRepublishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InBoundShipmentService inBoundShipmentService;

    @MockBean
    private CommonUtil commonUtil;

    @SpyBean
    private ObjectMapper objectMapper;

    private ShipmentEnterpriseMessage shipmentPayload;
    private String shipmentJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        shipmentPayload = new ShipmentEnterpriseMessage();
        // We use a real object for the request body now
        shipmentJson = new ObjectMapper().writeValueAsString(shipmentPayload);
    }

    @Test
    void createShipmentInTM_Success() throws Exception {
        String expectedPayload = "{\"audit\":\"payload\"}";
        doReturn(expectedPayload).when(objectMapper).writeValueAsString(any(ShipmentEnterpriseMessage.class));
        doNothing().when(inBoundShipmentService).createShipment(any(ShipmentEnterpriseMessage.class));
        doNothing().when(commonUtil).createAndPublishFeedbackAuditMessage(anyString(), anyString());

        mockMvc.perform(post("/api/v1/createShipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipmentJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Success"));
    }

    @Test
    void createShipmentInTM_RequestBodyNotReadable_ReturnsBadRequest() throws Exception {
        String malformedJson = "{\"key\":\"value\""; // Incomplete JSON

        mockMvc.perform(post("/api/v1/createShipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShipmentInTM_SerializationFailure_ReturnsBadRequest() throws Exception {
        doThrow(new JsonProcessingException("Serialization error") {})
                .when(objectMapper).writeValueAsString(any(ShipmentEnterpriseMessage.class));

        mockMvc.perform(post("/api/v1/createShipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipmentJson)) // Content is valid, but serialization inside the method will fail
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request payload format."));
    }

    @Test
    void createShipmentInTM_InternalServerError() throws Exception {
        doThrow(new RuntimeException("Database connection failed"))
                .when(inBoundShipmentService).createShipment(any(ShipmentEnterpriseMessage.class));

        mockMvc.perform(post("/api/v1/createShipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipmentJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to create shipment due to an internal error."));
    }
}
