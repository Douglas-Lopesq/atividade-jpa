package com.example.biblioteca.controller;

import com.example.biblioteca.dto.CategoriaLivrosResumo;
import com.example.biblioteca.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/contagem-livros")
    public List<CategoriaLivrosResumo> contarLivros() {
        return categoriaRepository.contarLivrosPorCategoria();
    }
}