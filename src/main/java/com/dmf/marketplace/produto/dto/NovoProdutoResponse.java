package com.dmf.marketplace.produto.dto;

import com.dmf.marketplace.produto.Produto;

import java.math.BigDecimal;
import java.util.List;

public record NovoProdutoResponse(
        Long id,
        String nome,
        BigDecimal valor,
        int quantidadeEstoque,
        List<NovaCaracteristicaResponse> caracteristicas,
        String descricao,
        Long idCategoria,
        Long idUsuario
) {
    public NovoProdutoResponse(Produto produto) {
        this(
                produto.getId(),
                produto.getNome(),
                produto.getValor(),
                produto.getQuantidadeEstoque(),
                produto.getCaracteristicas().stream()
                        .map(NovaCaracteristicaResponse::new)
                        .toList(),
                produto.getDescricao(),
                produto.getCategoria().getId(),
                produto.getDono().getId()
        );
    }
}
