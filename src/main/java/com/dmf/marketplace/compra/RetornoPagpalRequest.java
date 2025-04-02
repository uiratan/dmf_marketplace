package com.dmf.marketplace.compra;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RetornoPagpalRequest(
        @NotBlank String idTransacao,
        @Min(0) @Max(1) int statusRetorno
) implements RetornoGatewayPagamento {

    public Transacao toTransacao(Compra compra) {
        return new Transacao(idTransacao, this.normalizaStatus(), compra);
    }

    private StatusTransacao normalizaStatus() {
        return statusRetorno == 1 ? StatusTransacao.SUCESSO : StatusTransacao.FALHA;
    }

}
