package com.example.biblioteca.controller;

import com.example.biblioteca.model.Livro;
import com.example.biblioteca.repository.LivroRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping("/disponiveis")
    public List<Livro> listarDisponiveis() {
        return livroRepository.findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(0);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public List<Livro> buscarPorCategoria(@PathVariable String nomeCategoria) {
        return livroRepository.buscarPorNomeCategoria(nomeCategoria);
    }

    @GetMapping("/autor")
    public List<Livro> buscarPorAutor(@RequestParam String nome) {
        return livroRepository.buscarPorNomeAutor(nome);
    }
}