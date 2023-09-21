package com.biopark.disk_bpk.controller;

import com.biopark.disk_bpk.model.CargoDTO;
import com.biopark.disk_bpk.service.CargoService;
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
@RequestMapping("/cargos")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(final CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("cargos", cargoService.findAll());
        return "cargo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("cargo") final CargoDTO cargoDTO) {
        return "cargo/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("cargo") @Valid final CargoDTO cargoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "cargo/add";
        }
        cargoService.create(cargoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("cargo.create.success"));
        return "redirect:/cargos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("cargo", cargoService.get(id));
        return "cargo/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("cargo") @Valid final CargoDTO cargoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "cargo/edit";
        }
        cargoService.update(id, cargoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("cargo.update.success"));
        return "redirect:/cargos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = cargoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            cargoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("cargo.delete.success"));
        }
        return "redirect:/cargos";
    }

}
