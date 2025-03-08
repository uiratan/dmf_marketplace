package com.dmf.marketplace.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NovoUsuarioRequest {

    @NotBlank
    @Email
    private final String login;

    @NotBlank
    @Size(min = 6)
    private final String senha;

    public NovoUsuarioRequest(final String login, final String senha) {
        System.out.println("============ DTO ");
        this.login = login;
        this.senha = senha;
    }

    public Usuario toModel() {
        return new Usuario(login, senha);
    }


    @Override
    public String toString() {
        return "NovoUsuarioRequest{" +
                "login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}

