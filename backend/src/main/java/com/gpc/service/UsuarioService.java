package com.gpc.service;

import com.gpc.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService
{
    private final List<Usuario> usuarios = new ArrayList();
    
    private Long proximoId = 1L;
    
    public Usuario cadastrar(Usuario usuario)
    {
        usuario.setId(proximoId);
        proximoId++;
        
        usuarios.add(usuario);
        
        return usuario;
    }
    
    public List<Usuario> listar()
    {
        return usuarios;
    }
}
