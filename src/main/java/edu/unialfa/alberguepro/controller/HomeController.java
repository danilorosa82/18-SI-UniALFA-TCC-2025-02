package edu.unialfa.alberguepro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // Retorna a página index.html
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; // Retorna a página login.html
    }
}