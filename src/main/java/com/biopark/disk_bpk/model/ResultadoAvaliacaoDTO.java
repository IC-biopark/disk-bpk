package com.biopark.disk_bpk.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultadoAvaliacaoDTO {
    
    private String perfilDoAluno;

    private  Long dominancia; 
    private  Long influencia;
    private  Long estabilidade;
    private  Long conformidade;

}
