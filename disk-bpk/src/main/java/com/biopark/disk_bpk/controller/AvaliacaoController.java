package com.biopark.disk_bpk.controller;

import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.AvaliacaoDTO;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.service.AvaliacaoService;
import com.biopark.disk_bpk.util.CustomCollectors;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/avaliacaos")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final PerguntaRepository perguntaRepository;
    private final UsuarioRepository usuarioRepository;

    public AvaliacaoController(final AvaliacaoService avaliacaoService,
            final PerguntaRepository perguntaRepository,
            final UsuarioRepository usuarioRepository) {
        this.avaliacaoService = avaliacaoService;
        this.perguntaRepository = perguntaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("perguntaListValues", perguntaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Pergunta::getId, Pergunta::getQuestao)));
        model.addAttribute("usuariosQueFinalizaramValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getNome)));
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

}
