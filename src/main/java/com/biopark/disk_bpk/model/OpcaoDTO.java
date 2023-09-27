package com.biopark.disk_bpk.model;

import com.biopark.disk_bpk.domain.enums.ValorDiskOpcaoEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OpcaoDTO {

    @NotNull
    @Size(max = 255)
    private String descricao;

    @NotNull
    private ValorDiskOpcaoEnum valorDisk;
}
