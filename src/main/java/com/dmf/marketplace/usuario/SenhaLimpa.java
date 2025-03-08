package com.dmf.marketplace.usuario;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

/**
 * Representa uma senha limpa no sistema
 * @author Uiratan Cavalcante
 */
public class SenhaLimpa {

    private final String senha;

    public SenhaLimpa(String senha) {
        Assert.hasText(senha, "senha é obrigatória");
        Assert.isTrue(senha.length() >= 6, "senha precisa de no mínimo 6 caracteres");

        this.senha = senha;
    }

    public String hash() {
        return new BCryptPasswordEncoder().encode(senha);
    }
}
