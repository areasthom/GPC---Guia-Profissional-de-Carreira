package com.gpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EsqueciSenhaResponse {

    /** Mensagem neutra: é a mesma exista ou não a conta (evita descobrir e-mails cadastrados). */
    private String mensagem;

    /** Só preenchido quando gpc.recuperacao.modo-dev=true (sem servidor de e-mail). */
    private String tokenDev;
}
