package com.biopark.disk_bpk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoFinalizadaDTO {
    private Long id;

    private String titulo;

    private String descricao;
    
    private String usuario;

    private Long usuarioId;
}
