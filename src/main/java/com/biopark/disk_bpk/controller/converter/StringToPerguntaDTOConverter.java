package com.biopark.disk_bpk.controller.converter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.biopark.disk_bpk.model.PerguntaDTO;

@Component
public class StringToPerguntaDTOConverter implements Converter<String, PerguntaDTO> {

    @Override
    public PerguntaDTO convert(String source) {
        PerguntaDTO perguntaDTO = new PerguntaDTO();
        perguntaDTO.setId(Long.parseLong(source));
        return perguntaDTO;
    }
}
