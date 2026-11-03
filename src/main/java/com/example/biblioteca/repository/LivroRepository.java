package com.example.biblioteca.repository;

import com.example.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @EntityGraph(attributePaths = {"categoria", "autores"})
    List<Livro> findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(Integer quantidade);

    @EntityGraph(attributePaths = {"categoria", "autores"})
    @Query("select l from Livro l where lower(l.categoria.nome) = lower(:nomeCategoria)")
    List<Livro> buscarPorNomeCategoria(@Param("nomeCategoria") String nomeCategoria);

    @EntityGraph(attributePaths = {"categoria", "autores"})
    @Query("select distinct l from Livro l join l.autores a where lower(a.nome) = lower(:nomeAutor) order by l.anoPublicacao asc")
    List<Livro> buscarPorNomeAutor(@Param("nomeAutor") String nomeAutor);
}