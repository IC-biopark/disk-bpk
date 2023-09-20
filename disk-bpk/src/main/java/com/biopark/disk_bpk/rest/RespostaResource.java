package com.biopark.disk_bpk.rest;

import com.biopark.disk_bpk.model.RespostaDTO;
import com.biopark.disk_bpk.service.RespostaService;
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
@RequestMapping(value = "/api/respostas", produces = MediaType.APPLICATION_JSON_VALUE)
public class RespostaResource {

    private final RespostaService respostaService;

    public RespostaResource(final RespostaService respostaService) {
        this.respostaService = respostaService;
    }

    @GetMapping
    public ResponseEntity<List<RespostaDTO>> getAllRespostas() {
        return ResponseEntity.ok(respostaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaDTO> getResposta(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(respostaService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createResposta(@RequestBody @Valid final RespostaDTO respostaDTO) {
        final Long createdId = respostaService.create(respostaDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateResposta(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final RespostaDTO respostaDTO) {
        respostaService.update(id, respostaDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteResposta(@PathVariable(name = "id") final Long id) {
        respostaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
