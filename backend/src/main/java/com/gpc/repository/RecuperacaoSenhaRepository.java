package com.gpc.repository;

import com.gpc.model.RecuperacaoSenha;
import com.gpc.model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecuperacaoSenhaRepository
        extends JpaRepository<RecuperacaoSenha, Long> {

    Optional<RecuperacaoSenha> findByTokenHash(String tokenHash);

    List<RecuperacaoSenha> findByUsuarioAndUsadoEmIsNull(Usuario usuario);

    boolean existsByUsuarioAndCriadoEmAfter(Usuario usuario, LocalDateTime instante);
}
