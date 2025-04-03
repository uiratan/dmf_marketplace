package com.dmf.marketplace.outrossistemas;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RankingNovaCompraRequest(
        @NotNull UUID idCompra,
        @NotNull Long idDonoProduto
) {
}
