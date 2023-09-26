package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AvaliacaoDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String titulo;

    @NotNull
    @Size(max = 100)
    private String descricao;

    @NotNull
    private List<Long> perguntaList;

    private List<Long> usuariosQueFinalizaram;

    @NotNull
    private List<Long> turmaList;

}
