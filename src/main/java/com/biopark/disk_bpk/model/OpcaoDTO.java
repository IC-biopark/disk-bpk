package com.biopark.disk_bpk.model;

import com.biopark.disk_bpk.domain.Opcao;
import com.biopark.disk_bpk.domain.enums.ValorDiskOpcaoEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpcaoDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String descricao;

    @NotNull
    private ValorDiskOpcaoEnum valorDisk;

    public OpcaoDTO(Opcao opcao) {
        this.id = opcao.getId();
        this.descricao = opcao.getDescricao();
        this.valorDisk = opcao.getValorDisk();
    }
}
