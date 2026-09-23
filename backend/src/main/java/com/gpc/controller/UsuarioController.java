package com.gpc.controller;

import com.gpc.model.Usuario;
import com.gpc.service.UsuarioService;
import java.util.List;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario)
    {
        Usuario usuarioCadastrado = usuarioService.cadastrar(usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCadastrado);
    }
    
    @GetMapping
    public List<Usuario> listar()
    {
        return usuarioService.listar();
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarErroValidacao(IllegalArgumentException erro)
    {
        return ResponseEntity.badRequest().body(erro.getMessage());
    }
    
}
