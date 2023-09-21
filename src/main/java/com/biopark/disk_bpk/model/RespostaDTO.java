package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RespostaDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String descricao;

    @NotNull
    private Long pergunta;

    @NotNull
    private Long avaliacao;

}
