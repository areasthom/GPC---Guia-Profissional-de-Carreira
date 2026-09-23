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
        if(usuario.getNome() == null || usuario.getNome().isBlank())
        {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if(usuario.getEmail() == null || usuario.getEmail().isBlank())
        {
            throw new IllegalArgumentException("O email é obrigatório.");
        }
        
        for(Usuario usuarioExiste : usuarios)
        {
            if(usuarioExiste.getEmail().equalsIgnoreCase(usuario.getEmail()))
            {
                throw new IllegalArgumentException("O email já está cadastrado.");
            }
        }
        
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
