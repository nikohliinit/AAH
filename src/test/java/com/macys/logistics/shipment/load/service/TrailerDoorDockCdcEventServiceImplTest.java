package com.macys.logistics.shipment.load.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.logistics.shipment.load.dto.TrailerDoorDockCDCEvent;
import com.macys.logistics.shipment.load.dto.TrailerDoorDockMetaData;
import com.macys.logistics.shipment.load.dto.TrlrData;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleInputDTO;
import com.macys.logistics.shipment.load.util.CommonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrailerDoorDockCdcEventServiceImplTest {

    @InjectMocks
    private TrailerDoorDockCdcEventServiceImpl service;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TrailerDockDoorService trailerDockDoorService;

    @Mock
    CommonUtil commonUtil;

    private TrailerDoorDockCDCEvent event;
    private TrailerDoorDockMetaData metadata;
    private TrlrData data;



    @BeforeEach
    void setUp() {
        event = new TrailerDoorDockCDCEvent();
        metadata = new TrailerDoorDockMetaData();
        metadata.setTableName("TRLR");
        metadata.setCsn("12345");
        event.setMetadata(metadata);
        
        data = new TrlrData();
        data.setTrlrNbr(1001L);
        data.setTrlrStatNbr(10);
        data.setCurrLocNbr(500);
        ReflectionTestUtils.setField(service, "updateFields", Arrays.asList("currLocNbr","currYardNbr"));

    }

    @Test
    void testHandleRawPayload_Insert_Success() throws Exception {
        metadata.setOperationName("INSERT");
        event.setData(data);
        RuleInputDTO ruleInputDTO = null;
        // Mock ObjectMapper to return our constructed event
        when(objectMapper.readValue(any(String.class), any(Class.class)))
                .thenReturn(event);
        lenient().when(trailerDockDoorService.performDockDoorProcess(any(),any())).thenReturn(ruleInputDTO);

        String rawPayload = "{\"metadata\":{\"OperationName\":\"INSERT\"}}";
        assertDoesNotThrow(() -> service.processDockDoorCDCEvent(rawPayload));
    }

    @Test
    void testHandleRawPayload_Update_Success() throws Exception {
        metadata.setOperationName("UPDATE");
        event.setData(data);
        RuleInputDTO ruleInputDTO = null;
        TrlrData before = new TrlrData();
        before.setTrlrNbr(1001L);
        before.setTrlrStatNbr(5);
        event.setBefore(before);

        when(objectMapper.readValue(any(String.class), any(Class.class)))
                .thenReturn(event);
        lenient().when(trailerDockDoorService.performDockDoorProcess(any(),any())).thenReturn(ruleInputDTO);
        String rawPayload = "{\"metadata\":{\"OperationName\":\"UPDATE\"}}";
        assertDoesNotThrow(() -> service.processDockDoorCDCEvent(rawPayload));
    }

    @Test
    void testHandleRawPayload_Delete_Ignored() throws Exception {
        metadata.setOperationName("DELETE");
        event.setData(data);

        when(objectMapper.readValue(any(String.class), any(Class.class)))
                .thenReturn(event);

        String rawPayload = "{\"metadata\":{\"OperationName\":\"DELETE\"}}";
        assertDoesNotThrow(() -> service.processDockDoorCDCEvent(rawPayload));
    }

    @Test
    void testHandleRawPayload_NonTrlrTable_Ignored() throws Exception {
        metadata.setTableName("SHIPMENT");
        metadata.setOperationName("INSERT");
        event.setData(data);

        when(objectMapper.readValue(any(String.class), any(Class.class)))
                .thenReturn(event);

        String rawPayload = "{\"metadata\":{\"TableName\":\"SHIPMENT\"}}";
        assertDoesNotThrow(() -> service.processDockDoorCDCEvent(rawPayload));
    }

    @Test
    void testHandleRawPayload_ParsingError_ThrowsException() throws Exception {
        when(objectMapper.readValue(any(String.class), any(Class.class)))
                .thenThrow(new RuntimeException("JSON Parse Error"));

        String rawPayload = "invalid-json";
        assertThrows(IllegalStateException.class, () -> service.processDockDoorCDCEvent(rawPayload));
    }
}