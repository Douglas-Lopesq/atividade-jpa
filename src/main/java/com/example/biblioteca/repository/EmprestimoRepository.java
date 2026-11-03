package com.example.biblioteca.repository;

import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    @EntityGraph(attributePaths = {"usuario", "itens", "itens.livro", "itens.livro.categoria", "itens.livro.autores"})
    @Query("select distinct e from Emprestimo e where e.usuario.id = :usuarioId and e.status = :status")
    List<Emprestimo> buscarPorUsuarioEStatus(@Param("usuarioId") Long usuarioId,
                                             @Param("status") StatusEmprestimo status);

    @EntityGraph(attributePaths = {"usuario", "itens", "itens.livro", "itens.livro.categoria", "itens.livro.autores"})
    @Query("select distinct e from Emprestimo e where e.dataDevolucaoPrevista < :hoje and e.status = :status")
    List<Emprestimo> buscarAtrasadosAtivos(@Param("hoje") LocalDate hoje,
                                           @Param("status") StatusEmprestimo status);
}