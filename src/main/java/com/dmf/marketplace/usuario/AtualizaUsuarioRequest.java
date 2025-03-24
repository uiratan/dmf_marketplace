package com.dmf.marketplace.usuario;

public record AtualizaUsuarioRequest(
        String nome,
        String login,
        String senha
) {
}
