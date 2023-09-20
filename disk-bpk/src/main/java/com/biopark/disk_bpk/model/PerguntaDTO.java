package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PerguntaDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String questao;

    @NotNull
    @Size(max = 70)
    private String ajuda;

    @NotNull
    private TipoPergunta tipoPergunta;

}
