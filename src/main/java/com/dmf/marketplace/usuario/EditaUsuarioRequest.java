package com.dmf.marketplace.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EditaUsuarioRequest(
        @NotBlank @Email String login,
        @NotBlank @Length(min = 6) String senha,

        String nome
) {

}
