package com.example.biblioteca.controller;

import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.StatusEmprestimo;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, EmprestimoRepository emprestimoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @GetMapping
    public List<Usuario> buscarPorNome(@RequestParam String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    @GetMapping("/{usuarioId}/emprestimos/ativos")
    public List<Emprestimo> listarEmprestimosAtivos(@PathVariable Long usuarioId) {
        return emprestimoRepository.buscarPorUsuarioEStatus(usuarioId, StatusEmprestimo.ATIVO);
    }
}