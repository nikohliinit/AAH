package com.macys.logistics.shipment.load.rule.executor;


import com.macys.logistics.shipment.load.dto.dockdoor.Action;
import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.RuleResultDTO;
import com.macys.logistics.shipment.load.service.TrailerDockDoorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class DockDoorActionExecutorTest {

    private TrailerDockDoorService tmClient;
    private DockDoorActionExecutor executor;

    @BeforeEach
    void setUp() {
        tmClient = mock(TrailerDockDoorService.class);
        executor = new DockDoorActionExecutor(tmClient);
    }

    // -----------------------------
    // EARLY RETURNS
    // -----------------------------

    @Test
    void execute_shouldReturn_whenResultIsNull() {
        executor.execute(null);
        verifyNoInteractions(tmClient);
    }

    @Test
    void execute_shouldReturn_whenActionIsNull() {
        RuleResultDTO result = mock(RuleResultDTO.class);
        when(result.getAction()).thenReturn(null);

        executor.execute(result);

        verifyNoInteractions(tmClient);
    }

    // -----------------------------
    // ATTACH
    // -----------------------------

    @Test
    void execute_shouldAttachDockDoor() {
        RuleResultDTO result = mock(RuleResultDTO.class);

        when(result.getAction()).thenReturn(Action.ATTACH);
        when(result.getTargetLoadId()).thenReturn("L1");
        when(result.getDockDoor()).thenReturn("D1");

        executor.execute(result);

        verify(tmClient).processDockDoorAttach("L1", "D1");
    }

    // -----------------------------
    // UPDATE
    // -----------------------------

    @Test
    void execute_shouldUpdateDockDoor() {
        RuleResultDTO result = mock(RuleResultDTO.class);

        when(result.getAction()).thenReturn(Action.UPDATE);
        when(result.getOldDockDoorReference()).thenReturn(Collections.singletonList("OLD_REF"));
        when(result.getTargetLoadId()).thenReturn("L2");
        when(result.getDockDoor()).thenReturn("D2");

        executor.execute(result);

        verify(tmClient).processDockDoorUpdate(
                "OLD_REF",
                "L2",
                "D2"
        );
    }

    // -----------------------------
    // DETACH — MULTIPLE LOADS
    // -----------------------------

    @Test
    void execute_shouldDetachMultipleLoads() {
        LoadDTO load1 = mockLoad("L1", "REF1", "D1");
        LoadDTO load2 = mockLoad("L2", "REF2", "D2");

        RuleResultDTO result = mock(RuleResultDTO.class);
        when(result.getAction()).thenReturn(Action.DETACH);
        when(result.getLoadsToDetach()).thenReturn(List.of(load1, load2));

        executor.execute(result);

        verify(tmClient).processDeleteDockDoor(Collections.singletonList("REF1"), "L1", "D1");
        verify(tmClient).processDeleteDockDoor(Collections.singletonList("REF2"), "L2", "D2");
    }

    // -----------------------------
    // DETACH — SINGLE (NULL LIST)
    // -----------------------------

    @Test
    void execute_shouldDetachSingleLoad_whenLoadsToDetachIsNull() {
        RuleResultDTO result = mock(RuleResultDTO.class);

        when(result.getAction()).thenReturn(Action.DETACH);
        when(result.getLoadsToDetach()).thenReturn(null);
        when(result.getOldDockDoorReference()).thenReturn(Collections.singletonList("REF3"));
        when(result.getTargetLoadId()).thenReturn("L3");
        when(result.getDockDoor()).thenReturn("D3");

        executor.execute(result);

        verify(tmClient).processDeleteDockDoor(
                Collections.singletonList("REF3"),
                "L3",
                "D3"
        );
    }

    // -----------------------------
    // NO_ACTION
    // -----------------------------

    @Test
    void execute_shouldDoNothing_forNoAction() {
        RuleResultDTO result = mock(RuleResultDTO.class);
        when(result.getAction()).thenReturn(Action.NO_ACTION);

        executor.execute(result);

        verifyNoInteractions(tmClient);
    }

    // -----------------------------
    // HELPER
    // -----------------------------

    private LoadDTO mockLoad(String loadId, String ref, String dockDoor) {
        LoadDTO load = mock(LoadDTO.class);
        when(load.getLoadId()).thenReturn(loadId);
        when(load.getSystemReferenceNumberIdOfExistingDockDoor()).thenReturn(Collections.singletonList(ref));
        when(load.getDockDoor()).thenReturn(dockDoor);
        return load;
    }
}

