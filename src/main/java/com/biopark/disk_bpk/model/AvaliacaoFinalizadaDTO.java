package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoFinalizadaDTO {
    private Long id;

    @NotNull
    @Size(max = 50)
    private String titulo;

    @NotNull
    @Size(max = 100)
    private String descricao;
    
    private String usuario;
}
