package com.biopark.disk_bpk.rest;

import com.biopark.disk_bpk.model.CargoDTO;
import com.biopark.disk_bpk.service.CargoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/cargos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoResource {

    private final CargoService cargoService;

    public CargoResource(final CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public ResponseEntity<List<CargoDTO>> getAllCargos() {
        return ResponseEntity.ok(cargoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDTO> getCargo(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(cargoService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCargo(@RequestBody @Valid final CargoDTO cargoDTO) {
        final Long createdId = cargoService.create(cargoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCargo(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CargoDTO cargoDTO) {
        cargoService.update(id, cargoDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCargo(@PathVariable(name = "id") final Long id) {
        cargoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
