package com.example.biblioteca.repository;

import com.example.biblioteca.dto.CategoriaLivrosResumo;
import com.example.biblioteca.model.Autor;
import com.example.biblioteca.model.Categoria;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.ItemEmprestimo;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusEmprestimo;
import com.example.biblioteca.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConsultasRepositoryTest {

    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private EmprestimoRepository emprestimoRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Categoria fantasia;
    private Autor autora;
    private Livro livroDisponivel;
    private Usuario usuario;

    @BeforeEach
    void prepararDados() {
        fantasia = new Categoria();
        fantasia.setNome("Fantasia");
        fantasia.setDescricao("Ficcao fantastica");
        categoriaRepository.save(fantasia);

        autora = new Autor();
        autora.setNome("Ana Martins");
        autorRepository.save(autora);

        livroDisponivel = criarLivro("Aventura", 2019, 2, fantasia, autora);
        livroRepository.save(livroDisponivel);
        livroRepository.save(criarLivro("Biblioteca", 2021, 0, fantasia, autora));

        usuario = new Usuario();
        usuario.setNome("Joao Silva");
        usuario.setCpf("123");
        usuario.setEmail("joao@example.com");
        usuarioRepository.save(usuario);
    }

    @Test
    void consultaLivrosDisponiveisFiltraEOrdena() {
        List<Livro> livros = livroRepository.findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(0);

        assertThat(livros).extracting(Livro::getTitulo).containsExactly("Aventura");
    }

    @Test
    void consultasPorCategoriaAutorEParteDoNomeFuncionamSemDiferenciarCaixa() {
        assertThat(livroRepository.buscarPorNomeCategoria("fantasia")).hasSize(2);
        assertThat(livroRepository.buscarPorNomeAutor("ANA MARTINS"))
                .extracting(Livro::getTitulo).containsExactly("Aventura", "Biblioteca");
        assertThat(usuarioRepository.findByNomeContainingIgnoreCase("silva"))
                .extracting(Usuario::getNome).containsExactly("Joao Silva");
    }

    @Test
    void consultasDeEmprestimoCarregamLivrosEFiltramAtrasoAtivo() {
        Emprestimo emprestimoAtivo = salvarEmprestimo(LocalDate.now().plusDays(1), StatusEmprestimo.ATIVO);
        Emprestimo emprestimoAtrasado = salvarEmprestimo(LocalDate.now().minusDays(4), StatusEmprestimo.ATIVO);

        assertThat(emprestimoRepository.buscarPorUsuarioEStatus(usuario.getId(), StatusEmprestimo.ATIVO))
                .hasSize(2)
                .allSatisfy(emprestimo -> assertThat(emprestimo.getItens()).hasSize(1));
        assertThat(emprestimoRepository.buscarAtrasadosAtivos(LocalDate.now(), StatusEmprestimo.ATIVO))
                .extracting(Emprestimo::getId)
                .contains(emprestimoAtrasado.getId())
                .doesNotContain(emprestimoAtivo.getId());
    }

    @Test
    void agregacaoContaLivrosPorCategoriaEmOrdemDecrescente() {
        Categoria tecnologia = new Categoria();
        tecnologia.setNome("Tecnologia");
        tecnologia.setDescricao("Computacao");
        categoriaRepository.save(tecnologia);
        livroRepository.save(criarLivro("Codigo", 2023, 1, tecnologia, autora));

        List<CategoriaLivrosResumo> resultado = categoriaRepository.contarLivrosPorCategoria();

        assertThat(resultado).extracting(CategoriaLivrosResumo::nomeCategoria)
                .containsExactly("Fantasia", "Tecnologia");
        assertThat(resultado).extracting(CategoriaLivrosResumo::quantidadeLivros)
                .containsExactly(2L, 1L);
    }

            @Test
            void tabelaLivroAutorPossuiChavePrimariaComposta() {
            Integer chavesPrimarias = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.table_constraints " +
                    "where table_schema = 'PUBLIC' and table_name = 'LIVRO_AUTOR' " +
                    "and constraint_type = 'PRIMARY KEY'",
                Integer.class);

            assertThat(chavesPrimarias).isEqualTo(1);
            }

    private Livro criarLivro(String titulo, int ano, int disponiveis, Categoria categoria, Autor autor) {
        Livro livro = new Livro();
        livro.setIsbn("isbn-" + titulo);
        livro.setTitulo(titulo);
        livro.setEditora("Editora Exemplo");
        livro.setAnoPublicacao(ano);
        livro.setNumeroPaginas(100);
        livro.setQuantidadeTotal(2);
        livro.setQuantidadeDisponivel(disponiveis);
        livro.setCategoria(categoria);
        livro.getAutores().add(autor);
        return livro;
    }

    private Emprestimo salvarEmprestimo(LocalDate dataDevolucao, StatusEmprestimo status) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setDataEmprestimo(LocalDate.now().minusDays(5));
        emprestimo.setDataDevolucaoPrevista(dataDevolucao);
        emprestimo.setStatus(status);
        emprestimo.setValorMulta(BigDecimal.ZERO);
        ItemEmprestimo item = new ItemEmprestimo();
        item.setEmprestimo(emprestimo);
        item.setLivro(livroDisponivel);
        emprestimo.getItens().add(item);
        return emprestimoRepository.save(emprestimo);
    }
}