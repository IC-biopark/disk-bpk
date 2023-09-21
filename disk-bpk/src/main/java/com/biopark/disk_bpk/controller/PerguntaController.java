package com.biopark.disk_bpk.controller;

import com.biopark.disk_bpk.model.PerguntaDTO;
import com.biopark.disk_bpk.model.TipoPergunta;
import com.biopark.disk_bpk.service.PerguntaService;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/perguntas")
public class PerguntaController {

    private final PerguntaService perguntaService;

    public PerguntaController(final PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("tipoPerguntaValues", TipoPergunta.values());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("perguntas", perguntaService.findAll());
        return "pergunta/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pergunta") final PerguntaDTO perguntaDTO) {
        return "pergunta/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("pergunta") @Valid final PerguntaDTO perguntaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pergunta/add";
        }
        perguntaService.create(perguntaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pergunta.create.success"));
        return "redirect:/perguntas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("pergunta", perguntaService.get(id));
        return "pergunta/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("pergunta") @Valid final PerguntaDTO perguntaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pergunta/edit";
        }
        perguntaService.update(id, perguntaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pergunta.update.success"));
        return "redirect:/perguntas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = perguntaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            perguntaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pergunta.delete.success"));
        }
        return "redirect:/perguntas";
    }

}
