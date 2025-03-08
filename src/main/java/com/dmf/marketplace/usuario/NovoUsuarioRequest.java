package com.dmf.marketplace.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class NovoUsuarioRequest {

    //         *  * O login não pode ser em branco ou nula
    //         *  * O login precisa ter o formato do email
    @NotBlank
    @Email
    private final String login;

    //        *  * A senha não pode ser branca ou nula
    //        *  * A senha precisa ter no mínimo 6 caracteres
    //        *  * A senha deve ser guardada usando algum algoritmo de hash da sua escolha.
    @NotBlank
    @Size(min = 6)
    private final String senha;

    //         *  * O instante não pode ser nulo e não pode ser no futuro
//    @NotBlank
    private final Instant createdAt;

    public NovoUsuarioRequest(final String login, final String senha) {

        this.login = login;
        this.senha = senha;
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "NovoUsuarioRequest{" +
                "login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

/**
 *  * necessidades
 *  * precisamos saber o instante do cadastro, login e senha.
 *  * restrições
 */