package com.gpc.controller;

import com.gpc.model.Usuario;
import com.gpc.service.UsuarioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController
{
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService)
    {
        this.usuarioService = usuarioService;
    }
    
    @PostMapping
    public Usuario cadastrar(@RequestBody Usuario usuario)
    {
        return usuarioService.cadastrar(usuario);
    }
    
    @GetMapping
    public List<Usuario> listar()
    {
        return usuarioService.listar();
    }
}
