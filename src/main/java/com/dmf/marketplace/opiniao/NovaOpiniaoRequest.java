package com.dmf.marketplace.opiniao;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NovaOpiniaoRequest(
        @Min(1) @Max(5) int nota,
        @NotBlank String titulo,
        @NotBlank
        @Size(max = 500)
        String descricao
) {
    public Opiniao toModel(
            EntityManager manager,
            Produto produto,
            Usuario consumidor) {
        return new Opiniao(nota, titulo, descricao, consumidor, produto);
    }
}
