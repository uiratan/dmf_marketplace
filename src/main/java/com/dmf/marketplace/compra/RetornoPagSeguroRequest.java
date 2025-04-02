package com.dmf.marketplace.compra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RetornoPagSeguroRequest(
        @NotBlank String idTransacao,
        @NotNull StatusRetornoPagSeguro statusRetorno
) implements RetornoGatewayPagamento {

    public Transacao toTransacao(Compra compra) {
        return new Transacao(idTransacao, statusRetorno.normalizaStatus(), compra);
    }
}
