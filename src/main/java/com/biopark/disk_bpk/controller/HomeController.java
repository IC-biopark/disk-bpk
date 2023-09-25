package com.biopark.disk_bpk.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    
    /**
     * Esta função lida com a solicitação GET para a URL raiz ("/") e retorna o nome da visualização a ser renderizada.
     *
     * @param  model             o objeto Model usado para passar dados para a visualização
     * @param  authentication    o objeto Authentication que representa os detalhes de autenticação do usuário atual
     * @return                   o nome da visualização a ser renderizada ("home/index")
     */
    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("user", authentication.getPrincipal());
        }
        return "home/index";
    }

    @GetMapping("/login")
    public String login(){
        return "/login";
    }

}
