package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Resposta;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.AvaliacaoDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.repos.RespostaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PerguntaRepository perguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RespostaRepository respostaRepository;

    public AvaliacaoService(final AvaliacaoRepository avaliacaoRepository,
            final PerguntaRepository perguntaRepository, final UsuarioRepository usuarioRepository,
            final RespostaRepository respostaRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.perguntaRepository = perguntaRepository;
        this.usuarioRepository = usuarioRepository;
        this.respostaRepository = respostaRepository;
    }

    public List<AvaliacaoDTO> findAll() {
        final List<Avaliacao> avaliacaos = avaliacaoRepository.findAll(Sort.by("id"));
        return avaliacaos.stream()
                .map(avaliacao -> mapToDTO(avaliacao, new AvaliacaoDTO()))
                .toList();
    }

    public AvaliacaoDTO get(final Long id) {
        return avaliacaoRepository.findById(id)
                .map(avaliacao -> mapToDTO(avaliacao, new AvaliacaoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AvaliacaoDTO avaliacaoDTO) {
        final Avaliacao avaliacao = new Avaliacao();
        mapToEntity(avaliacaoDTO, avaliacao);
        return avaliacaoRepository.save(avaliacao).getId();
    }

    public void update(final Long id, final AvaliacaoDTO avaliacaoDTO) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(avaliacaoDTO, avaliacao);
        avaliacaoRepository.save(avaliacao);
    }

    public void delete(final Long id) {
        avaliacaoRepository.deleteById(id);
    }

    private AvaliacaoDTO mapToDTO(final Avaliacao avaliacao, final AvaliacaoDTO avaliacaoDTO) {
        avaliacaoDTO.setId(avaliacao.getId());
        avaliacaoDTO.setTitulo(avaliacao.getTitulo());
        avaliacaoDTO.setDescricao(avaliacao.getDescricao());
        avaliacaoDTO.setPerguntaList(avaliacao.getPerguntaList().stream()
                .map(pergunta -> pergunta.getId())
                .toList());
        avaliacaoDTO.setUsuariosQueFinalizaram(avaliacao.getUsuariosQueFinalizaram().stream()
                .map(usuario -> usuario.getId())
                .toList());
        return avaliacaoDTO;
    }

    private Avaliacao mapToEntity(final AvaliacaoDTO avaliacaoDTO, final Avaliacao avaliacao) {
        avaliacao.setTitulo(avaliacaoDTO.getTitulo());
        avaliacao.setDescricao(avaliacaoDTO.getDescricao());
        final List<Pergunta> perguntaList = perguntaRepository.findAllById(
                avaliacaoDTO.getPerguntaList() == null ? Collections.emptyList() : avaliacaoDTO.getPerguntaList());
        if (perguntaList.size() != (avaliacaoDTO.getPerguntaList() == null ? 0 : avaliacaoDTO.getPerguntaList().size())) {
            throw new NotFoundException("one of perguntaList not found");
        }
        avaliacao.setPerguntaList(perguntaList.stream().collect(Collectors.toSet()));
        final List<Usuario> usuariosQueFinalizaram = usuarioRepository.findAllById(
                avaliacaoDTO.getUsuariosQueFinalizaram() == null ? Collections.emptyList() : avaliacaoDTO.getUsuariosQueFinalizaram());
        if (usuariosQueFinalizaram.size() != (avaliacaoDTO.getUsuariosQueFinalizaram() == null ? 0 : avaliacaoDTO.getUsuariosQueFinalizaram().size())) {
            throw new NotFoundException("one of usuariosQueFinalizaram not found");
        }
        avaliacao.setUsuariosQueFinalizaram(usuariosQueFinalizaram.stream().collect(Collectors.toSet()));
        return avaliacao;
    }

    public String getReferencedWarning(final Long id) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Resposta avaliacaoResposta = respostaRepository.findFirstByAvaliacao(avaliacao);
        if (avaliacaoResposta != null) {
            return WebUtils.getMessage("avaliacao.resposta.avaliacao.referenced", avaliacaoResposta.getId());
        }
        return null;
    }

}
