package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsuarioDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String nome;

    @NotNull
    @Size(max = 70)
    private String segundoNome;

    @NotNull
    @Size(max = 50)
    private String email;

    @NotNull
    @Size(max = 50)
    private String senha;

    private List<Long> turmaList;

    @NotNull
    private Long cargo;

    private Long respostaList;

}
