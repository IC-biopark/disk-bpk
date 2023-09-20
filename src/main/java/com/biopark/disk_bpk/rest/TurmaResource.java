package com.biopark.disk_bpk.rest;

import com.biopark.disk_bpk.model.TurmaDTO;
import com.biopark.disk_bpk.service.TurmaService;
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
@RequestMapping(value = "/api/turmas", produces = MediaType.APPLICATION_JSON_VALUE)
public class TurmaResource {

    private final TurmaService turmaService;

    public TurmaResource(final TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping
    public ResponseEntity<List<TurmaDTO>> getAllTurmas() {
        return ResponseEntity.ok(turmaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> getTurma(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(turmaService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTurma(@RequestBody @Valid final TurmaDTO turmaDTO) {
        final Long createdId = turmaService.create(turmaDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTurma(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TurmaDTO turmaDTO) {
        turmaService.update(id, turmaDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTurma(@PathVariable(name = "id") final Long id) {
        turmaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
