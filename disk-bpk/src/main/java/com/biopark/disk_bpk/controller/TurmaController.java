package com.biopark.disk_bpk.controller;

import com.biopark.disk_bpk.model.TurmaDTO;
import com.biopark.disk_bpk.service.TurmaService;
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
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(final TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("turmas", turmaService.findAll());
        return "turma/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("turma") final TurmaDTO turmaDTO) {
        return "turma/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("turma") @Valid final TurmaDTO turmaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "turma/add";
        }
        turmaService.create(turmaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("turma.create.success"));
        return "redirect:/turmas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("turma", turmaService.get(id));
        return "turma/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("turma") @Valid final TurmaDTO turmaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "turma/edit";
        }
        turmaService.update(id, turmaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("turma.update.success"));
        return "redirect:/turmas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = turmaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            turmaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("turma.delete.success"));
        }
        return "redirect:/turmas";
    }

}
