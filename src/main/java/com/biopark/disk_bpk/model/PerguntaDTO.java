package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.biopark.disk_bpk.domain.Pergunta;


@Getter
@Setter
@NoArgsConstructor
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
    private List<OpcaoDTO> opcoes = new ArrayList<>();

    private RespostaDTO resposta;

    public PerguntaDTO (Pergunta pergunta) {
        this.id = pergunta.getId();
        this.questao = pergunta.getQuestao();
        this.ajuda = pergunta.getAjuda();
        this.tipoPergunta = pergunta.getTipoPergunta();
        this.opcoes = pergunta.getOpcoes().stream().map(OpcaoDTO::new).toList();
    }
}
