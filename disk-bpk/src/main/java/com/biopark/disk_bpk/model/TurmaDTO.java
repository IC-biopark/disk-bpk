package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TurmaDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String nome;

    @Size(max = 25)
    private String periodo;

}
