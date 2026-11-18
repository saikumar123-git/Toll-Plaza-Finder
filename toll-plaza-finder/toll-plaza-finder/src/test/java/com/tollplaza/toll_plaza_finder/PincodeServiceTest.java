
package com.tollplaza.toll_plaza_finder;

import com.tollplaza.toll_plaza_finder.service.PincodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PincodeServiceTest {

    private PincodeService pincodeService;

    @BeforeEach
    void setUp() {
        pincodeService = new PincodeService();
    }

    @Test
    void shouldReturnCoordinatesForValidPincode() {

        double[] coordinates =
                pincodeService.getCoordinates("560064");

        assertNotNull(coordinates);
        assertEquals(13.1155, coordinates[0]);
        assertEquals(77.6070, coordinates[1]);
    }

    @Test
    void shouldRejectInvalidPincode() {

        assertThrows(
                IllegalArgumentException.class,
                () -> pincodeService.getCoordinates("123")
        );
    }

    @Test
    void shouldRejectUnknownPincode() {

        assertThrows(
                IllegalArgumentException.class,
                () -> pincodeService.getCoordinates("500001")
        );
    }

    @Test
    void shouldRejectNullPincode() {

        assertThrows(
                IllegalArgumentException.class,
                () -> pincodeService.getCoordinates(null)
        );
    }
}
