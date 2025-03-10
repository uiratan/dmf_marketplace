package com.dmf.marketplace.usuario;

import com.dmf.marketplace.compartilhado.ExcludeFromJacocoGeneratedReport;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

//1
public class NovoUsuarioRequest {

    @NotBlank
    @Email
    private final String login;

    @NotBlank
    @Length(min = 6)
    private final String senha;

    public NovoUsuarioRequest(final String login, final String senha) {
        this.login = login;
        this.senha = senha;
    }

    //1
    public Usuario toModel() {
        return new Usuario(login, new SenhaLimpa(senha));
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public String toString() {
        return "NovoUsuarioRequest{" +
                "login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}

