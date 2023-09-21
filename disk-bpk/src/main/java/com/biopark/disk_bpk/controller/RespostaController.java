package com.biopark.disk_bpk.controller;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.model.RespostaDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.PerguntaRepository;
import com.biopark.disk_bpk.service.RespostaService;
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
@RequestMapping("/respostas")
public class RespostaController {

    private final RespostaService respostaService;
    private final PerguntaRepository perguntaRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public RespostaController(final RespostaService respostaService,
            final PerguntaRepository perguntaRepository,
            final AvaliacaoRepository avaliacaoRepository) {
        this.respostaService = respostaService;
        this.perguntaRepository = perguntaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("perguntaValues", perguntaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Pergunta::getId, Pergunta::getQuestao)));
        model.addAttribute("avaliacaoValues", avaliacaoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Avaliacao::getId, Avaliacao::getTitulo)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("respostas", respostaService.findAll());
        return "resposta/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("resposta") final RespostaDTO respostaDTO) {
        return "resposta/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("resposta") @Valid final RespostaDTO respostaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "resposta/add";
        }
        respostaService.create(respostaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("resposta.create.success"));
        return "redirect:/respostas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("resposta", respostaService.get(id));
        return "resposta/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("resposta") @Valid final RespostaDTO respostaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "resposta/edit";
        }
        respostaService.update(id, respostaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("resposta.update.success"));
        return "redirect:/respostas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = respostaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            respostaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("resposta.delete.success"));
        }
        return "redirect:/respostas";
    }

}
