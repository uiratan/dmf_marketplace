package com.dmf.marketplace.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

//1
public record NovoUsuarioRequest(
        @NotBlank @Email String login,
        @NotBlank @Length(min = 6) String senha) {

    //1
    public Usuario toModel() {
        return new Usuario(login, new SenhaLimpa(senha));
    }

}

