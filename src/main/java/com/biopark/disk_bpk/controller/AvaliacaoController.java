package com.biopark.disk_bpk.controller;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Turma;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.AvaliacaoDTO;
import com.biopark.disk_bpk.model.AvaliacaoFinalizadaDTO;
import com.biopark.disk_bpk.model.PerguntaDTO;
import com.biopark.disk_bpk.model.ResultadoAvaliacaoDTO;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.repos.TurmaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.service.AvaliacaoService;
import com.biopark.disk_bpk.service.RespostaService;
import com.biopark.disk_bpk.util.CustomCollectors;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
@RequestMapping("/avaliacaos")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final PerguntaRepository perguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;
    private final RespostaService respostaService;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("perguntaListValues", perguntaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Pergunta::getId, Pergunta::getQuestao)));
        model.addAttribute("usuariosQueFinalizaramValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getNome)));
        model.addAttribute("turmaListValues", turmaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Turma::getId, Turma::getNome)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("avaliacaos", avaliacaoService.findAll());
        return "avaliacao/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("avaliacao") final AvaliacaoDTO avaliacaoDTO) {
        return "avaliacao/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("avaliacao") @Valid final AvaliacaoDTO avaliacaoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "avaliacao/add";
        }
        // TODO - enviar email para pessoas que participam da turma que possuem uma nova
        // avaliação
        avaliacaoService.create(avaliacaoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("avaliacao.create.success"));
        return "redirect:/avaliacaos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("avaliacao", avaliacaoService.get(id));
        return "avaliacao/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("avaliacao") @Valid final AvaliacaoDTO avaliacaoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "avaliacao/edit";
        }
        avaliacaoService.update(id, avaliacaoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("avaliacao.update.success"));
        return "redirect:/avaliacaos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = avaliacaoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            avaliacaoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("avaliacao.delete.success"));
        }
        return "redirect:/avaliacaos";
    }

    @GetMapping("avaliacoes-para-responder/{id}")
    public String avaliacoesParaResponder(final Model model, @PathVariable final Long id) {
        model.addAttribute("avaliacoes", avaliacaoService.findAvaliacoesParaResponder(id));
        return "avaliacao/avaliacoes-para-responder";
    }

    @GetMapping("resposta-avaliacao/{id}")
    public String respostaAvaliacao(final Model model, @PathVariable final Long id) {
        AvaliacaoDTO avaliacao = avaliacaoService.get(id);
        model.addAttribute("avaliacao", avaliacao);
        return "avaliacao/resposta-avaliacao";
    }

    /**
     * Finaliza uma avaliação para um usuario especifico
     * 
     * @param id
     * @param avaliacao
     * @param bindingResult
     * @param redirectAttributes
     * @param authentication
     * @return
     * @throws Exception
     */
    @PostMapping("resposta-avaliacao/{id}")
    public String responderAvaliacao(@PathVariable final Long id, @ModelAttribute AvaliacaoDTO avaliacao,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes, Authentication authentication) throws Exception {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (bindingResult.hasErrors()) {
            return "/avaliacaos/avaliacoes-para-responder/" + usuario.getId();
        }

        try {
            avaliacaoService.finalizarAvaliacao(avaliacao, usuario);
            /**
             * Salva a resposta de cada pergunta
             */
            for (PerguntaDTO pergunta : avaliacao.getPerguntaList()) {
                pergunta.getResposta().setAvaliacao(id);
                pergunta.getResposta().setPergunta(pergunta.getId());
                pergunta.getResposta().setUsuario(usuario.getId());
                respostaService.create(pergunta.getResposta());
            }
        } catch (Exception e) {
            throw new Exception("Não foi possível salvar a avaliação");
        }

        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("avaliacao.finalizada.success"));
        return "redirect:/avaliacaos/avaliacoes-para-responder/" + usuario.getId();
    }

    @GetMapping("resultados/{id}")
    public String resultados(@PathVariable final Long id, final Model model) {
        AvaliacaoDTO avaliacao = avaliacaoService.get(id);
        List<AvaliacaoFinalizadaDTO> avaliacoesFinalizadas = new ArrayList<>();
        avaliacao.getUsuariosQueFinalizaram().forEach(usuarioId -> {
            Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
            AvaliacaoFinalizadaDTO avaliacaoDoUsuario = new AvaliacaoFinalizadaDTO();
            List<Avaliacao> avaliacaoFinalizadaOpt = usuario.getAvaliacoesFinalizadas();
            if (avaliacaoFinalizadaOpt != null && avaliacaoFinalizadaOpt.size() > 0) {
                for (Avaliacao avaliacaoFinalizada : avaliacaoFinalizadaOpt) {
                    if (avaliacaoFinalizada.getId().equals(id)) {
                        avaliacaoDoUsuario.setDescricao(avaliacaoFinalizada.getDescricao());
                        avaliacaoDoUsuario.setTitulo(avaliacaoFinalizada.getTitulo());
                        avaliacaoDoUsuario.setUsuario(usuario.getNome());
                        avaliacaoDoUsuario.setId(avaliacaoFinalizada.getId());
                        avaliacaoDoUsuario.setUsuarioId(usuarioId);
                        avaliacoesFinalizadas.add(avaliacaoDoUsuario);
                    }
                }
            }
        });
        model.addAttribute("avaliacoes", avaliacoesFinalizadas);
        return "avaliacao/resultados";
    }

    @GetMapping("resultado/{id}/{usuarioid}")
    public String visualizarResultadoDoUsuario (@PathVariable(name = "id") final Long avaliacaoId, @PathVariable(name = "usuarioid") Long usuarioId, Model model) {
        ResultadoAvaliacaoDTO resultadoAvaliacao = avaliacaoService.analisaResultadoDaAvaliacao(avaliacaoId, usuarioId);
        model.addAttribute("resultado", resultadoAvaliacao);
        return "avaliacao/resultado-do-aluno";
    }
}