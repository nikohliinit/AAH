package com.macys.logistics.shipment.load.service;

import com.macys.logistics.shipment.load.dto.dockdoor.LoadDTO;
import com.macys.logistics.shipment.load.dto.dockdoor.CleanupResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DockDoorCleanupProcessorServiceImplTest {

    private DockDoorCleanupProcessorServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DockDoorCleanupProcessorServiceImpl();
    }

    // ---------------------------------
    // HAPPY PATH — REMOVE LOAD
    // ---------------------------------

    @Test
    void cleanup_shouldDetachAndRemoveLoad_whenDockDoorExistsAndTrailerDiffers() {
        LoadDTO load = mockLoad("TRAILER_2", "D1");
        List<LoadDTO> loads = new ArrayList<>(List.of(load));

        CleanupResult result = service.cleanup("TRAILER_1", loads);

        assertEquals(1, result.getLoadsToDetach().size());
        assertTrue(loads.isEmpty());
    }

    // ---------------------------------
    // KEEP LOAD — SAME TRAILER
    // ---------------------------------

    @Test
    void cleanup_shouldKeepLoad_whenTrailerMatchesEventTrailer() {
        LoadDTO load = mockLoad("TRAILER_1", "D1");
        List<LoadDTO> loads = new ArrayList<>(List.of(load));

        CleanupResult result = service.cleanup("TRAILER_1", loads);

        assertFalse(result.getLoadsToDetach().isEmpty());
        assertEquals(0, loads.size());
    }

    // ---------------------------------
    // KEEP LOAD — NO DOCK DOOR
    // ---------------------------------

    @Test
    void cleanup_shouldKeepLoad_whenDockDoorIsNull() {
        LoadDTO load = mockLoad("TRAILER_2", null);
        List<LoadDTO> loads = new ArrayList<>(List.of(load));

        CleanupResult result = service.cleanup("TRAILER_1", loads);

        assertTrue(result.getLoadsToDetach().isEmpty());
        assertEquals(1, loads.size());
    }

    // ---------------------------------
    // MULTIPLE LOADS — MIXED CASES
    // ---------------------------------

    @Test
    void cleanup_shouldDetachOnlyInvalidLoads() {
        LoadDTO detachLoad = mockLoad("TRAILER_2", "D1");
        LoadDTO keepLoad1 = mockLoad("TRAILER_1", "D2");
        LoadDTO keepLoad2 = mockLoad("TRAILER_3", null);

        List<LoadDTO> loads = new ArrayList<>(
                List.of(detachLoad, keepLoad1, keepLoad2)
        );

        CleanupResult result = service.cleanup("TRAILER_1", loads);

        assertEquals(2, result.getLoadsToDetach().size());
        assertEquals(1, loads.size());
        assertFalse(loads.contains(detachLoad));
    }

    // ---------------------------------
    // EMPTY LIST
    // ---------------------------------

    @Test
    void cleanup_shouldHandleEmptyLoadList() {
        List<LoadDTO> loads = new ArrayList<>();

        CleanupResult result = service.cleanup("TRAILER_1", loads);

        assertTrue(result.getLoadsToDetach().isEmpty());
        assertTrue(loads.isEmpty());
    }

    // ---------------------------------
    // HELPER
    // ---------------------------------

    private LoadDTO mockLoad(String trailerNumber, String dockDoor) {
        LoadDTO load = mock(LoadDTO.class);
        when(load.getTrailerNumber()).thenReturn(trailerNumber);
        when(load.getDockDoor()).thenReturn(dockDoor);
        return load;
    }
}
