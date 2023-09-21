package com.biopark.disk_bpk.rest;

import com.biopark.disk_bpk.model.PerguntaDTO;
import com.biopark.disk_bpk.service.PerguntaService;
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
@RequestMapping(value = "/api/perguntas", produces = MediaType.APPLICATION_JSON_VALUE)
public class PerguntaResource {

    private final PerguntaService perguntaService;

    public PerguntaResource(final PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @GetMapping
    public ResponseEntity<List<PerguntaDTO>> getAllPerguntas() {
        return ResponseEntity.ok(perguntaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerguntaDTO> getPergunta(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(perguntaService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPergunta(@RequestBody @Valid final PerguntaDTO perguntaDTO) {
        final Long createdId = perguntaService.create(perguntaDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePergunta(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PerguntaDTO perguntaDTO) {
        perguntaService.update(id, perguntaDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePergunta(@PathVariable(name = "id") final Long id) {
        perguntaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
