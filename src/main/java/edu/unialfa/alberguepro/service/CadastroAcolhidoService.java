package edu.unialfa.alberguepro.service;

import edu.unialfa.alberguepro.model.CadastroAcolhido;
import edu.unialfa.alberguepro.repository.CadastroAcolhidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CadastroAcolhidoService {

    @Autowired
    private CadastroAcolhidoRepository repository;

    public void salvar(CadastroAcolhido cadastroAcolhido) {
        repository.save(cadastroAcolhido);
    }

    public List<CadastroAcolhido> listarTodos() {
        return repository.findAll();
    }

    public Page<CadastroAcolhido> listarTodosPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public CadastroAcolhido buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Acolhido não encontrado: " + id));
    }

    public List<CadastroAcolhido> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Page<CadastroAcolhido> buscarPorNomePaginado(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public void deletarPorId(Long id) {
        repository.deleteById(id);
    }

    public boolean cpfJaExiste(String cpf) {
        return repository.existsByCpf(cpf);
    }

    public List<CadastroAcolhido> buscarAcolhidosPermanenciaProlongada(Integer dias) {
        LocalDate dataLimite = LocalDate.now().minusDays(dias);
        return repository.findByDataIngressoBeforeAndDataSaidaIsNullOrderByDataIngressoAsc(dataLimite);
    }

    public List<CadastroAcolhido> listarAcolhidosSemLeitoAtivo() {
        return repository.findAcolhidosSemLeitoAtivo();
    }

    public Page<CadastroAcolhido> buscarComFiltros(String nome, String sexo, Integer idadeMin, Integer idadeMax, Pageable pageable) {
        List<CadastroAcolhido> todosAcolhidos = repository.findAll();
        
        java.util.stream.Stream<CadastroAcolhido> stream = todosAcolhidos.stream();
        
        if (nome != null && !nome.trim().isEmpty()) {
            stream = stream.filter(a -> a.getNome().toLowerCase().contains(nome.toLowerCase()));
        }
        
        if (sexo != null && !sexo.trim().isEmpty()) {
            stream = stream.filter(a -> sexo.equalsIgnoreCase(a.getSexo().name()));
        }
        
        if (idadeMin != null) {
            stream = stream.filter(a -> a.getIdade() != null && a.getIdade() >= idadeMin);
        }
        
        if (idadeMax != null) {
            stream = stream.filter(a -> a.getIdade() != null && a.getIdade() <= idadeMax);
        }
        
        List<CadastroAcolhido> filtrados = stream.collect(java.util.stream.Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtrados.size());
        List<CadastroAcolhido> paginatedList = filtrados.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(paginatedList, pageable, filtrados.size());
    }
}
