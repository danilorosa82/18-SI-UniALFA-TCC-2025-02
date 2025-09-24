package edu.unialfa.alberguepro.controller;

import edu.unialfa.alberguepro.dto.DashboardDTO;
import edu.unialfa.alberguepro.model.ControlePatrimonio;
import edu.unialfa.alberguepro.model.Leito;
import edu.unialfa.alberguepro.model.Produto;
import edu.unialfa.alberguepro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private CadastroAcolhidoRepository cadastroAcolhidoRepository;

    @Autowired
    private LeitoRepository leitoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ControlePatrimonioRepository controlePatrimonioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/")
    public String index(Model model) {
        DashboardDTO dashboardDTO = new DashboardDTO();

        // Acolhidos
        dashboardDTO.setTotalAcolhidos(cadastroAcolhidoRepository.countByDataSaidaIsNull());

        // Leitos
        dashboardDTO.setLeitosOcupados(leitoRepository.countByAcolhidoIsNotNull());
        dashboardDTO.setLeitosLivres(leitoRepository.countByAcolhidoIsNull());
        dashboardDTO.setTotalLeitos(leitoRepository.count());

        // Quartos
        long quartosOcupados = leitoRepository.countDistinctQuartoByAcolhidoIsNotNull();
        long totalQuartos = Leito.Quarto.values().length;
        dashboardDTO.setQuartosOcupados(quartosOcupados);
        dashboardDTO.setQuartosLivres(totalQuartos - quartosOcupados);
        dashboardDTO.setTotalQuartos(totalQuartos);

        // Usuarios
        dashboardDTO.setTotalUsuarios(usuarioRepository.count());

        // Patrimonio
        List<ControlePatrimonio> patrimonios = controlePatrimonioRepository.findAll();
        Map<String, Long> patrimonioPorStatus = patrimonios.stream()
                .collect(Collectors.groupingBy(ControlePatrimonio::getStatus, Collectors.counting()));
        dashboardDTO.setPatrimonioPorStatus(patrimonioPorStatus);

        // Estoque
        List<Produto> produtosBaixoEstoque = produtoRepository.findTop5ByOrderByQuantidadeAsc();
        Map<String, Integer> estoqueBaixo = produtosBaixoEstoque.stream()
                .collect(Collectors.toMap(Produto::getNome, Produto::getQuantidade));
        dashboardDTO.setEstoqueBaixo(estoqueBaixo);


        model.addAttribute("dashboard", dashboardDTO);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
