package com.dmf.marketplace.outrossistemas;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NovaCompraNFRequest(
        @NotNull UUID idCompra,
        @NotNull Long idComprador
) {
}
