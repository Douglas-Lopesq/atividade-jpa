package com.example.biblioteca.repository;

import com.example.biblioteca.dto.CategoriaLivrosResumo;
import com.example.biblioteca.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("select new com.example.biblioteca.dto.CategoriaLivrosResumo(c.nome, count(l.id)) " +
            "from Categoria c left join c.livros l " +
            "group by c.id, c.nome " +
            "order by count(l.id) desc, c.nome asc")
    List<CategoriaLivrosResumo> contarLivrosPorCategoria();
}