package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


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

    @NotNull
    private List<String> opcoes;

}
