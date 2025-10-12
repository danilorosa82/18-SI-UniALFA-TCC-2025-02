package edu.unialfa.alberguepro.controller;

import edu.unialfa.alberguepro.model.Quarto;
import edu.unialfa.alberguepro.service.QuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quartos")
public class QuartoController {

    @Autowired
    private QuartoService service;

    @GetMapping("/novo")
    public String iniciarCadastro(Model model) {
        model.addAttribute("quarto", new Quarto());
        return "quarto/form"; // Sua página Thymeleaf
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Quarto quarto, RedirectAttributes attributes) {

        try {
            service.salvar(quarto);
            attributes.addFlashAttribute("mensagemSucesso", "Quarto salvo com sucesso!");
            return "redirect:/quartos/lista";
        } catch (IllegalArgumentException e) {
            // Adiciona a mensagem de erro e redireciona para o formulário
            attributes.addFlashAttribute("mensagemErro", e.getMessage());
            // Se for novo, volta para a tela de cadastro, se for edição, volta para a lista
            return "redirect:/quartos/novo";
        }

       // service.salvar(quarto);

        // return "redirect:/quartos/lista";
    }

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("quartos", service.listarTodos());
        return "quarto-lista"; // Sua página Thymeleaf
    }

    @GetMapping("/editar/{id}")
    public String iniciarEdicao(@PathVariable Long id, Model model) {
        Quarto quarto = service.buscarPorId(id);
        if (quarto == null) {
            return "redirect:/quartos/lista"; // Ou página 404
        }
        model.addAttribute("quarto", quarto);
        return "quarto/form"; // Usa o mesmo formulário de cadastro
    }


    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        service.deletar(id);
        // Lembre-se que o CascadeType.ALL na Entidade Quarto garantirá
        // que os Leitos e Vagas associadas sejam deletadas!
        return "redirect:/quartos/lista";
    }

}