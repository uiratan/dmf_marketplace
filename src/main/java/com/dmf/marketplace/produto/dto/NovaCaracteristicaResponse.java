package com.dmf.marketplace.produto.dto;

import com.dmf.marketplace.produto.CaracteristicaProduto;

public record NovaCaracteristicaResponse(
        Long id,
        String caracteristica,
        String descricao
) {
    public NovaCaracteristicaResponse(CaracteristicaProduto caracteristica) {
        this(
                caracteristica.getId(),
                caracteristica.getNome(),
                caracteristica.getDescricao()
        );
    }
}
