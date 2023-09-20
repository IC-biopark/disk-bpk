package com.biopark.disk_bpk.rest;

import com.biopark.disk_bpk.model.AvaliacaoDTO;
import com.biopark.disk_bpk.service.AvaliacaoService;
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
@RequestMapping(value = "/api/avaliacaos", produces = MediaType.APPLICATION_JSON_VALUE)
public class AvaliacaoResource {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoResource(final AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoDTO>> getAllAvaliacaos() {
        return ResponseEntity.ok(avaliacaoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoDTO> getAvaliacao(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(avaliacaoService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAvaliacao(
            @RequestBody @Valid final AvaliacaoDTO avaliacaoDTO) {
        final Long createdId = avaliacaoService.create(avaliacaoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAvaliacao(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AvaliacaoDTO avaliacaoDTO) {
        avaliacaoService.update(id, avaliacaoDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAvaliacao(@PathVariable(name = "id") final Long id) {
        avaliacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
