package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Resposta;
import com.biopark.disk_bpk.domain.Turma;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.domain.enums.ValorDiskOpcaoEnum;
import com.biopark.disk_bpk.model.AvaliacaoDTO;
import com.biopark.disk_bpk.model.AvaliacaoFinalizadaDTO;
import com.biopark.disk_bpk.model.PerguntaDTO;
import com.biopark.disk_bpk.model.ResultadoAvaliacaoDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.repos.RespostaRepository;
import com.biopark.disk_bpk.repos.TurmaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PerguntaRepository perguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RespostaRepository respostaRepository;
    private final TurmaRepository turmaRepository;

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
        avaliacaoDTO.setPerguntaList(avaliacao.getPerguntaList().stream().map(PerguntaDTO::new).toList());
        avaliacaoDTO.setUsuariosQueFinalizaram(avaliacao.getUsuariosQueFinalizaram().stream()
                .map(usuario -> usuario.getId())
                .toList());
        return avaliacaoDTO;
    }

    private Avaliacao mapToEntity(final AvaliacaoDTO avaliacaoDTO, final Avaliacao avaliacao) {
        avaliacao.setTitulo(avaliacaoDTO.getTitulo());
        avaliacao.setDescricao(avaliacaoDTO.getDescricao());
        final List<Pergunta> perguntaList = perguntaRepository.findAllById(
                avaliacaoDTO.getPerguntaList() == null ? Collections.emptyList()
                        : avaliacaoDTO.getPerguntaList().stream().map(p -> p.getId()).toList());
        if (perguntaList
                .size() != (avaliacaoDTO.getPerguntaList() == null ? 0 : avaliacaoDTO.getPerguntaList().size())) {
            throw new NotFoundException("one of perguntaList not found");
        }
        avaliacao.setPerguntaList(perguntaList.stream().collect(Collectors.toSet()));
        final List<Usuario> usuariosQueFinalizaram = usuarioRepository.findAllById(
                avaliacaoDTO.getUsuariosQueFinalizaram() == null ? Collections.emptyList()
                        : avaliacaoDTO.getUsuariosQueFinalizaram());
        if (usuariosQueFinalizaram.size() != (avaliacaoDTO.getUsuariosQueFinalizaram() == null ? 0
                : avaliacaoDTO.getUsuariosQueFinalizaram().size())) {
            throw new NotFoundException("one of usuariosQueFinalizaram not found");
        }
        avaliacao.setUsuariosQueFinalizaram(usuariosQueFinalizaram.stream().collect(Collectors.toSet()));
        final List<Turma> turmaList = turmaRepository.findAllById(
                avaliacaoDTO.getTurmaList() == null ? Collections.emptyList() : avaliacaoDTO.getTurmaList());
        avaliacao.setTurmaList(turmaList);
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

    /**
     * Retorna a uma lista de avaliações que o usuario tem que responder.
     * O critério para saber as avaliações que ele tem que responder é as turmas as
     * quais ele participa
     * 
     * @param id - Id do usuario logado
     */
    public List<Avaliacao> findAvaliacoesParaResponder(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        List<Avaliacao> avaliacoesParaResponder = new ArrayList<>();

        // Retorna todas as avaliações que o usuario tem que responder de acordo com a
        // turma que ele esta
        for (Turma turma : usuario.getTurmaList()) {
            Avaliacao avaliacao = avaliacaoRepository.findByTurma(turma);
            if (avaliacao != null) {
                avaliacoesParaResponder.add(avaliacao);
            }
        }

        // Remove todas as avaliações que o usuario ja respondeu
        for (Avaliacao avaliacao : avaliacoesParaResponder) {
            if (avaliacao != null) {
                if (avaliacao.getUsuariosQueFinalizaram() != null) {
                    if (avaliacao.getUsuariosQueFinalizaram().contains(usuario)) {
                        avaliacoesParaResponder.remove(avaliacao);
                    }
                }
            }
        }

        return avaliacoesParaResponder;
    }

    public void finalizarAvaliacao(AvaliacaoDTO avaliacao, Usuario usuario) {
        Avaliacao avaliacaoFinalizada = avaliacaoRepository.findById(avaliacao.getId())
                .orElseThrow(() -> new ServiceException("Avaliação não encontrada"));
        avaliacaoFinalizada = mapToEntity(avaliacao, avaliacaoFinalizada);
        Usuario usuarioQueFinalizouAAvaliacao = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new ServiceException("Usuário não encontrado"));
        avaliacaoFinalizada.getUsuariosQueFinalizaram().add(usuarioQueFinalizouAAvaliacao);
        avaliacaoRepository.save(avaliacaoFinalizada);
    }

    public ResultadoAvaliacaoDTO analisaResultadoDaAvaliacao(Long avaliacaoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        List<Resposta> respostasDaAvaliacaoDoUsuario = usuario.getRespostaList().stream()
                .filter(resposta -> resposta.getAvaliacao().getId().equals(avaliacaoId)).toList();
        ResultadoAvaliacaoDTO resultadoAvaliacaoDTO = new ResultadoAvaliacaoDTO();

        Long dominancia = 0l;
        Long influencia = 0l;
        Long estabilidade = 0l;
        Long conformidade = 0l;

        for (Resposta resposta : respostasDaAvaliacaoDoUsuario) {
            if (resposta.getOpcao().getValorDisk() == ValorDiskOpcaoEnum.D) {
                dominancia++;
            } else if (resposta.getOpcao().getValorDisk() == ValorDiskOpcaoEnum.I) {
                influencia++;
            } else if (resposta.getOpcao().getValorDisk() == ValorDiskOpcaoEnum.S) {
                estabilidade++;
            } else if (resposta.getOpcao().getValorDisk() == ValorDiskOpcaoEnum.C) {
                conformidade++;
            }
        }

        resultadoAvaliacaoDTO.setDominancia(dominancia);
        resultadoAvaliacaoDTO.setInfluencia(influencia);
        resultadoAvaliacaoDTO.setEstbilidade(estabilidade);
        resultadoAvaliacaoDTO.setConformidade(conformidade);


        Long maiorValor = Math.max(Math.max(Math.max(dominancia, influencia), estabilidade), conformidade);

        if (maiorValor == dominancia) {
            resultadoAvaliacaoDTO.setPerfilDoAluno("Dominância");
        } else if (maiorValor == influencia) {
            resultadoAvaliacaoDTO.setPerfilDoAluno("Influência");
        } else if (maiorValor == estabilidade) {
            resultadoAvaliacaoDTO.setPerfilDoAluno("Estabilidade");
        } else if (maiorValor == conformidade) {
            resultadoAvaliacaoDTO.setPerfilDoAluno("Conformidade");
        }
        
        return resultadoAvaliacaoDTO;
    }

}