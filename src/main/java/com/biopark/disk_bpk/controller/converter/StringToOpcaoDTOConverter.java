package com.biopark.disk_bpk.controller.converter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.biopark.disk_bpk.model.OpcaoDTO;

@Component
public class StringToOpcaoDTOConverter implements Converter<String, OpcaoDTO> {

    @Override
    public OpcaoDTO convert(String source) {
        OpcaoDTO opcaoDTO = new OpcaoDTO();
        opcaoDTO.setId(Long.parseLong(source));
        return opcaoDTO;
    }
}
