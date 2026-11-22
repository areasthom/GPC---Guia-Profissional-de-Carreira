package com.gpc.service;

import com.gpc.dto.EsqueciSenhaResponse;
import com.gpc.model.RecuperacaoSenha;
import com.gpc.model.Usuario;
import com.gpc.repository.RecuperacaoSenhaRepository;
import com.gpc.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecuperacaoSenhaService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacaoSenhaService.class);

    static final String MENSAGEM_NEUTRA =
            "Se o e-mail estiver cadastrado, você receberá as instruções para redefinir a senha.";
    static final String TOKEN_INVALIDO =
            "Link de recuperação inválido ou expirado. Peça um novo.";

    private static final int SENHA_MIN = 6;
    private static final int SENHA_MAX = 72; // limite do BCrypt
    private static final int INTERVALO_ENTRE_PEDIDOS_SEG = 60;

    private final SecureRandom random = new SecureRandom();
    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoRepository;
    private final PasswordEncoder passwordEncoder;

    private final long validadeMinutos;
    private final boolean modoDev;
    private final String urlBase;

    public RecuperacaoSenhaService(
            UsuarioRepository usuarioRepository,
            RecuperacaoSenhaRepository recuperacaoRepository,
            PasswordEncoder passwordEncoder,
            @Value("${gpc.recuperacao.validade-minutos:30}") long validadeMinutos,
            @Value("${gpc.recuperacao.modo-dev:false}") boolean modoDev,
            @Value("${gpc.recuperacao.url-base:http://localhost:5500/redefinir-senha.html}") String urlBase) {

        this.usuarioRepository = usuarioRepository;
        this.recuperacaoRepository = recuperacaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.validadeMinutos = validadeMinutos;
        this.modoDev = modoDev;
        this.urlBase = urlBase;
    }

    @Transactional
    public EsqueciSenhaResponse solicitar(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail.");
        }

        Optional<Usuario> encontrado = usuarioRepository.findByEmailIgnoreCase(email.trim());

        // Sempre a mesma resposta: não revela se o e-mail existe.
        if (encontrado.isEmpty()
                || !"ATIVA".equalsIgnoreCase(encontrado.get().getStatusConta())) {
            return new EsqueciSenhaResponse(MENSAGEM_NEUTRA, null);
        }

        Usuario usuario = encontrado.get();

        // Anti-spam: no máximo um pedido por minuto para o mesmo usuário.
        if (recuperacaoRepository.existsByUsuarioAndCriadoEmAfter(
                usuario, LocalDateTime.now().minusSeconds(INTERVALO_ENTRE_PEDIDOS_SEG))) {
            return new EsqueciSenhaResponse(MENSAGEM_NEUTRA, null);
        }

        // Invalida pedidos anteriores ainda abertos: vale só o link mais recente.
        LocalDateTime agora = LocalDateTime.now();
        for (RecuperacaoSenha antigo : recuperacaoRepository.findByUsuarioAndUsadoEmIsNull(usuario)) {
            antigo.setUsadoEm(agora);
        }

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RecuperacaoSenha pedido = new RecuperacaoSenha();
        pedido.setUsuario(usuario);
        pedido.setTokenHash(hash(token));
        pedido.setExpiraEm(agora.plusMinutes(validadeMinutos));
        recuperacaoRepository.save(pedido);

        // Sem servidor de e-mail ainda: o link aparece no console do backend.
        log.info("[RECUPERAÇÃO DE SENHA] {} -> {}?token={} (expira em {} min)",
                usuario.getEmail(), urlBase, token, validadeMinutos);

        return new EsqueciSenhaResponse(MENSAGEM_NEUTRA, modoDev ? token : null);
    }

    @Transactional(readOnly = true)
    public void validarToken(String token) {
        buscarPedidoValido(token);
    }

    @Transactional
    public void redefinir(String token, String novaSenha) {

        if (novaSenha == null || novaSenha.length() < SENHA_MIN) {
            throw new IllegalArgumentException(
                    "A senha precisa ter pelo menos " + SENHA_MIN + " caracteres.");
        }
        if (novaSenha.length() > SENHA_MAX) {
            throw new IllegalArgumentException(
                    "A senha pode ter no máximo " + SENHA_MAX + " caracteres.");
        }

        RecuperacaoSenha pedido = buscarPedidoValido(token);
        Usuario usuario = pedido.getUsuario();

        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // Consome este e qualquer outro link aberto do usuário.
        LocalDateTime agora = LocalDateTime.now();
        pedido.setUsadoEm(agora);
        for (RecuperacaoSenha outro : recuperacaoRepository.findByUsuarioAndUsadoEmIsNull(usuario)) {
            outro.setUsadoEm(agora);
        }
    }

    private RecuperacaoSenha buscarPedidoValido(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(TOKEN_INVALIDO);
        }
        return recuperacaoRepository.findByTokenHash(hash(token.trim()))
                .filter(RecuperacaoSenha::estaValido)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_INVALIDO));
    }

    private static String hash(String token) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
