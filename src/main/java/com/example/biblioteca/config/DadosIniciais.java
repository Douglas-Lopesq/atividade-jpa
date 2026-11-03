package com.example.biblioteca.config;

import com.example.biblioteca.model.Autor;
import com.example.biblioteca.model.Categoria;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.ItemEmprestimo;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusEmprestimo;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.AutorRepository;
import com.example.biblioteca.repository.CategoriaRepository;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.LivroRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DadosIniciais {

    @Bean
    CommandLineRunner carregarDados(CategoriaRepository categoriaRepository,
                                    AutorRepository autorRepository,
                                    LivroRepository livroRepository,
                                    UsuarioRepository usuarioRepository,
                                    EmprestimoRepository emprestimoRepository) {
        return args -> {
            if (categoriaRepository.count() > 0) {
                return;
            }

            Categoria fantasia = novaCategoria("Fantasia", "Narrativas de mundos imaginarios.");
            Categoria tecnologia = novaCategoria("Tecnologia", "Computacao e desenvolvimento de software.");
            Categoria romance = novaCategoria("Romance", "Ficcao centrada em relacoes humanas.");
            categoriaRepository.saveAll(List.of(fantasia, tecnologia, romance));

            Autor ana = novoAutor("Ana Martins", "Brasileira", LocalDate.of(1980, 4, 12));
            Autor bruno = novoAutor("Bruno Costa", "Brasileiro", LocalDate.of(1975, 9, 3));
            Autor clara = novoAutor("Clara Souza", "Portuguesa", LocalDate.of(1988, 2, 20));
            autorRepository.saveAll(List.of(ana, bruno, clara));

            Livro livroFantasia = novoLivro("9780000000001", "A Cidade das Estrelas", "Horizonte", 2018, 320, 4, 3, fantasia, ana);
            Livro livroJava = novoLivro("9780000000002", "Java na Pratica", "Codigo Aberto", 2022, 450, 3, 2, tecnologia, bruno);
            Livro livroDados = novoLivro("9780000000003", "Fundamentos de Dados", "Codigo Aberto", 2020, 280, 2, 0, tecnologia, bruno);
            Livro livroRomance = novoLivro("9780000000004", "Cartas para Clara", "Horizonte", 2021, 240, 5, 4, romance, clara);
            livroFantasia.getAutores().add(bruno);
            livroRepository.saveAll(List.of(livroFantasia, livroJava, livroDados, livroRomance));

            Usuario joao = novoUsuario("Joao Silva", "11111111111", "joao@example.com", "11999990001");
            Usuario maria = novoUsuario("Maria Silva Santos", "22222222222", "maria@example.com", "11999990002");
            usuarioRepository.saveAll(List.of(joao, maria));

            Emprestimo ativo = novoEmprestimo(joao, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11), StatusEmprestimo.ATIVO);
            ativo.getItens().add(novoItem(ativo, livroJava));
            ativo.getItens().add(novoItem(ativo, livroFantasia));
            Emprestimo atrasado = novoEmprestimo(maria, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), StatusEmprestimo.ATIVO);
            atrasado.getItens().add(novoItem(atrasado, livroRomance));
            emprestimoRepository.saveAll(List.of(ativo, atrasado));
        };
    }

    private Categoria novaCategoria(String nome, String descricao) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setDescricao(descricao);
        return categoria;
    }

    private Autor novoAutor(String nome, String nacionalidade, LocalDate dataNascimento) {
        Autor autor = new Autor();
        autor.setNome(nome);
        autor.setNacionalidade(nacionalidade);
        autor.setDataNascimento(dataNascimento);
        autor.setBiografia("Autor de exemplo para demonstracao do sistema.");
        return autor;
    }

    private Livro novoLivro(String isbn, String titulo, String editora, int ano, int paginas,
                            int total, int disponiveis, Categoria categoria, Autor autor) {
        Livro livro = new Livro();
        livro.setIsbn(isbn);
        livro.setTitulo(titulo);
        livro.setEditora(editora);
        livro.setAnoPublicacao(ano);
        livro.setNumeroPaginas(paginas);
        livro.setQuantidadeTotal(total);
        livro.setQuantidadeDisponivel(disponiveis);
        livro.setCategoria(categoria);
        livro.getAutores().add(autor);
        return livro;
    }

    private Usuario novoUsuario(String nome, String cpf, String email, String telefone) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setDataCadastro(LocalDate.now());
        usuario.setEndereco("Endereco de exemplo");
        usuario.setAtivo(true);
        return usuario;
    }

    private Emprestimo novoEmprestimo(Usuario usuario, LocalDate emprestadoEm,
                                      LocalDate devolverAte, StatusEmprestimo status) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setDataEmprestimo(emprestadoEm);
        emprestimo.setDataDevolucaoPrevista(devolverAte);
        emprestimo.setStatus(status);
        emprestimo.setValorMulta(BigDecimal.ZERO);
        return emprestimo;
    }

    private ItemEmprestimo novoItem(Emprestimo emprestimo, Livro livro) {
        ItemEmprestimo item = new ItemEmprestimo();
        item.setEmprestimo(emprestimo);
        item.setLivro(livro);
        return item;
    }
}