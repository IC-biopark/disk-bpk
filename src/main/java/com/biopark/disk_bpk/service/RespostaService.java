package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Resposta;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.RespostaDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.repos.RespostaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;
    private final PerguntaRepository perguntaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public RespostaService(final RespostaRepository respostaRepository,
            final PerguntaRepository perguntaRepository,
            final AvaliacaoRepository avaliacaoRepository,
            final UsuarioRepository usuarioRepository) {
        this.respostaRepository = respostaRepository;
        this.perguntaRepository = perguntaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<RespostaDTO> findAll() {
        final List<Resposta> respostas = respostaRepository.findAll(Sort.by("id"));
        return respostas.stream()
                .map(resposta -> mapToDTO(resposta, new RespostaDTO()))
                .toList();
    }

    public RespostaDTO get(final Long id) {
        return respostaRepository.findById(id)
                .map(resposta -> mapToDTO(resposta, new RespostaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RespostaDTO respostaDTO) {
        final Resposta resposta = new Resposta();
        mapToEntity(respostaDTO, resposta);
        return respostaRepository.save(resposta).getId();
    }

    public void update(final Long id, final RespostaDTO respostaDTO) {
        final Resposta resposta = respostaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(respostaDTO, resposta);
        respostaRepository.save(resposta);
    }

    public void delete(final Long id) {
        respostaRepository.deleteById(id);
    }

    private RespostaDTO mapToDTO(final Resposta resposta, final RespostaDTO respostaDTO) {
        respostaDTO.setId(resposta.getId());
        respostaDTO.setDescricao(resposta.getDescricao());
        respostaDTO.setPergunta(resposta.getPergunta() == null ? null : resposta.getPergunta().getId());
        respostaDTO.setAvaliacao(resposta.getAvaliacao() == null ? null : resposta.getAvaliacao().getId());
        return respostaDTO;
    }

    private Resposta mapToEntity(final RespostaDTO respostaDTO, final Resposta resposta) {
        resposta.setDescricao(respostaDTO.getDescricao());
        final Pergunta pergunta = respostaDTO.getPergunta() == null ? null : perguntaRepository.findById(respostaDTO.getPergunta())
                .orElseThrow(() -> new NotFoundException("pergunta not found"));
        resposta.setPergunta(pergunta);
        final Avaliacao avaliacao = respostaDTO.getAvaliacao() == null ? null : avaliacaoRepository.findById(respostaDTO.getAvaliacao())
                .orElseThrow(() -> new NotFoundException("avaliacao not found"));
        resposta.setAvaliacao(avaliacao);
        return resposta;
    }

    public String getReferencedWarning(final Long id) {
        final Resposta resposta = respostaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Usuario respostaListUsuario = usuarioRepository.findFirstByRespostaList(resposta);
        if (respostaListUsuario != null) {
            return WebUtils.getMessage("resposta.usuario.respostaList.referenced", respostaListUsuario.getId());
        }
        return null;
    }

}
