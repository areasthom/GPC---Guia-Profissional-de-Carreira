package com.gpc.controller;

import com.gpc.dto.EsqueciSenhaRequest;
import com.gpc.dto.EsqueciSenhaResponse;
import com.gpc.dto.LoginRequest;
import com.gpc.dto.LoginResponse;
import com.gpc.dto.RedefinirSenhaRequest;
import com.gpc.service.AutenticacaoService;
import com.gpc.service.RecuperacaoSenhaService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public AutenticacaoController(
            AutenticacaoService autenticacaoService,
            RecuperacaoSenhaService recuperacaoSenhaService) {

        this.autenticacaoService = autenticacaoService;
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest dados) {

        return autenticacaoService.login(dados);
    }

    @PostMapping("/esqueci-senha")
    public EsqueciSenhaResponse esqueciSenha(
            @RequestBody EsqueciSenhaRequest dados) {

        return recuperacaoSenhaService.solicitar(dados.getEmail());
    }

    @GetMapping("/redefinir-senha/validar")
    public Map<String, Boolean> validarToken(
            @RequestParam String token) {

        recuperacaoSenhaService.validarToken(token);
        return Map.of("valido", true);
    }

    @PostMapping("/redefinir-senha")
    public Map<String, String> redefinirSenha(
            @RequestBody RedefinirSenhaRequest dados) {

        recuperacaoSenhaService.redefinir(dados.getToken(), dados.getNovaSenha());
        return Map.of("mensagem", "Senha redefinida com sucesso. Entre com a nova senha.");
    }
}
