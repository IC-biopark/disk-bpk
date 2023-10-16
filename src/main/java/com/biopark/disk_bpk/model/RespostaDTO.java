package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespostaDTO {

    private Long id;

    @NotNull
    private Long pergunta;

    @NotNull
    private Long avaliacao;

    private OpcaoDTO opcaoEscolhida;

    private Long usuario;

}
