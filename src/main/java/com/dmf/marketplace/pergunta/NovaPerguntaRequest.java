package com.dmf.marketplace.pergunta;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;

public record NovaPerguntaRequest(
        @NotBlank String titulo
) {
    public Pergunta toModel(
            EntityManager manager,
            Produto produto,
            Usuario consumidor) {
        return new Pergunta(titulo, consumidor, produto);
    }
}
