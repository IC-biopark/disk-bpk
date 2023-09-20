package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Resposta;
import com.biopark.disk_bpk.model.PerguntaDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.repos.RespostaRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class PerguntaService {

    private final PerguntaRepository perguntaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final RespostaRepository respostaRepository;

    public PerguntaService(final PerguntaRepository perguntaRepository,
            final AvaliacaoRepository avaliacaoRepository,
            final RespostaRepository respostaRepository) {
        this.perguntaRepository = perguntaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.respostaRepository = respostaRepository;
    }

    public List<PerguntaDTO> findAll() {
        final List<Pergunta> perguntas = perguntaRepository.findAll(Sort.by("id"));
        return perguntas.stream()
                .map(pergunta -> mapToDTO(pergunta, new PerguntaDTO()))
                .toList();
    }

    public PerguntaDTO get(final Long id) {
        return perguntaRepository.findById(id)
                .map(pergunta -> mapToDTO(pergunta, new PerguntaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PerguntaDTO perguntaDTO) {
        final Pergunta pergunta = new Pergunta();
        mapToEntity(perguntaDTO, pergunta);
        return perguntaRepository.save(pergunta).getId();
    }

    public void update(final Long id, final PerguntaDTO perguntaDTO) {
        final Pergunta pergunta = perguntaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(perguntaDTO, pergunta);
        perguntaRepository.save(pergunta);
    }

    public void delete(final Long id) {
        final Pergunta pergunta = perguntaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        avaliacaoRepository.findAllByPerguntaList(pergunta)
                .forEach(avaliacao -> avaliacao.getPerguntaList().remove(pergunta));
        perguntaRepository.delete(pergunta);
    }

    private PerguntaDTO mapToDTO(final Pergunta pergunta, final PerguntaDTO perguntaDTO) {
        perguntaDTO.setId(pergunta.getId());
        perguntaDTO.setQuestao(pergunta.getQuestao());
        perguntaDTO.setAjuda(pergunta.getAjuda());
        perguntaDTO.setTipoPergunta(pergunta.getTipoPergunta());
        return perguntaDTO;
    }

    private Pergunta mapToEntity(final PerguntaDTO perguntaDTO, final Pergunta pergunta) {
        pergunta.setQuestao(perguntaDTO.getQuestao());
        pergunta.setAjuda(perguntaDTO.getAjuda());
        pergunta.setTipoPergunta(perguntaDTO.getTipoPergunta());
        return pergunta;
    }

    public String getReferencedWarning(final Long id) {
        final Pergunta pergunta = perguntaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Avaliacao perguntaListAvaliacao = avaliacaoRepository.findFirstByPerguntaList(pergunta);
        if (perguntaListAvaliacao != null) {
            return WebUtils.getMessage("pergunta.avaliacao.perguntaList.referenced", perguntaListAvaliacao.getId());
        }
        final Resposta perguntaResposta = respostaRepository.findFirstByPergunta(pergunta);
        if (perguntaResposta != null) {
            return WebUtils.getMessage("pergunta.resposta.pergunta.referenced", perguntaResposta.getId());
        }
        return null;
    }

}
