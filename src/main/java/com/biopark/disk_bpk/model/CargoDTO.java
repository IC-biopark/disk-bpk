package com.biopark.disk_bpk.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CargoDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String nome;

}
