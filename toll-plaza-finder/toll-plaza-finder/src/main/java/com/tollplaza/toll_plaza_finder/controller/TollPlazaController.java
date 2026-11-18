
package com.tollplaza.toll_plaza_finder.controller;

import com.tollplaza.toll_plaza_finder.dto.TollPlazaRequest;
import com.tollplaza.toll_plaza_finder.dto.TollPlazaResponse;
import com.tollplaza.toll_plaza_finder.service.TollPlazaFinderService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/toll-plazas")
public class TollPlazaController {

    private final TollPlazaFinderService finderService;

    public TollPlazaController(
            TollPlazaFinderService finderService) {
        this.finderService = finderService;
    }

    @PostMapping
    public ResponseEntity<TollPlazaResponse> findTollPlazas(
            @Valid @RequestBody TollPlazaRequest request) {

        TollPlazaResponse response =
                finderService.findTollPlazas(request);

        return ResponseEntity.ok(response);
    }
}
