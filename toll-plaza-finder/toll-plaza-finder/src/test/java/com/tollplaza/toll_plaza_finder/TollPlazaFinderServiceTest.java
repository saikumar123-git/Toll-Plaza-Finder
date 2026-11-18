
package com.tollplaza.toll_plaza_finder;

import com.tollplaza.toll_plaza_finder.client.MapplsRoutingClient;
import com.tollplaza.toll_plaza_finder.dto.MapplsRouteData;
import com.tollplaza.toll_plaza_finder.dto.TollPlazaRequest;
import com.tollplaza.toll_plaza_finder.dto.TollPlazaResponse;
import com.tollplaza.toll_plaza_finder.service.MapplsRouteParser;
import com.tollplaza.toll_plaza_finder.service.PincodeService;
import com.tollplaza.toll_plaza_finder.service.TollPlazaFinderService;
import com.tollplaza.toll_plaza_finder.service.TollPlazaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.JsonNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TollPlazaFinderServiceTest {

    @Mock
    private MapplsRoutingClient mapplsRoutingClient;

    @Mock
    private MapplsRouteParser mapplsRouteParser;

    @Mock
    private TollPlazaService tollPlazaService;

    @Mock
    private PincodeService pincodeService;

    @InjectMocks
    private TollPlazaFinderService finderService;

    @Mock
    private JsonNode mapplsResponse;


    // Test 1: Reject same source and destination
    @Test
    void shouldRejectSameSourceAndDestination() {

        TollPlazaRequest request =
                new TollPlazaRequest("560064", "560064");

        assertThrows(
                IllegalArgumentException.class,
                () -> finderService.findTollPlazas(request)
        );

        verifyNoInteractions(
                mapplsRoutingClient,
                mapplsRouteParser,
                tollPlazaService,
                pincodeService
        );
    }


    // Test 2: Return empty list when no toll plazas are found
    @Test
    void shouldReturnEmptyListWhenNoTollPlazasFound() {

        when(pincodeService.getCoordinates("560064"))
                .thenReturn(new double[]{13.1155, 77.6070});

        when(pincodeService.getCoordinates("411045"))
                .thenReturn(new double[]{18.5590, 73.7868});

        when(mapplsRoutingClient.getRoute(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()
        )).thenReturn(mapplsResponse);

        when(mapplsRouteParser.parseRoute(mapplsResponse))
                .thenReturn(new MapplsRouteData(100.0, List.of()));

        when(tollPlazaService.findTollPlazasAlongRoute(anyList()))
                .thenReturn(List.of());

        TollPlazaRequest request =
                new TollPlazaRequest("560064", "411045");

        TollPlazaResponse response =
                finderService.findTollPlazas(request);

        assertNotNull(response);

        assertEquals(
                100.0,
                response.route().distanceInKm()
        );

        assertTrue(response.tollPlazas().isEmpty());

        verify(mapplsRoutingClient, times(1))
                .getRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble());

        verify(tollPlazaService, times(1))
                .findTollPlazasAlongRoute(anyList());
    }


    // Test 3: Return toll plazas for a valid route
    @Test
    void shouldReturnTollPlazasForValidRoute() {

        when(pincodeService.getCoordinates("560064"))
                .thenReturn(new double[]{13.1155, 77.6070});

        when(pincodeService.getCoordinates("411045"))
                .thenReturn(new double[]{18.5590, 73.7868});

        when(mapplsRoutingClient.getRoute(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()
        )).thenReturn(mapplsResponse);

        when(mapplsRouteParser.parseRoute(mapplsResponse))
                .thenReturn(new MapplsRouteData(851.8224, List.of()));

        List<TollPlazaResponse.TollPlazaInfo> tollPlazas =
                List.of(
                        new TollPlazaResponse.TollPlazaInfo(
                                "Tumkur Road Toll Plaza",
                                13.042116,
                                77.475715,
                                24.04
                        ),
                        new TollPlazaResponse.TollPlazaInfo(
                                "Nelamangala Toll Plaza",
                                13.0925202,
                                77.394433,
                                34.31
                        )
                );

        when(tollPlazaService.findTollPlazasAlongRoute(anyList()))
                .thenReturn(tollPlazas);

        TollPlazaRequest request =
                new TollPlazaRequest("560064", "411045");

        TollPlazaResponse response =
                finderService.findTollPlazas(request);

        assertNotNull(response);

        assertEquals(
                851.8224,
                response.route().distanceInKm()
        );

        assertEquals(2, response.tollPlazas().size());

        assertEquals(
                "Tumkur Road Toll Plaza",
                response.tollPlazas().get(0).name()
        );

        verify(mapplsRoutingClient, times(1))
                .getRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
}
