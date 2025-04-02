package com.dmf.marketplace.compra;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NovaCompraRequest {

    @NotNull private final Long idProduto;
    @NotNull @Positive private final Integer quantidade;
    private final GatewayPagamento gatewayPagamento;

    public NovaCompraRequest(Long idProduto, Integer quantidade, String gatewayPagamento) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.gatewayPagamento = GatewayPagamento.fromString(gatewayPagamento); // Conversão personalizada
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public GatewayPagamento getGatewayPagamento() {
        return gatewayPagamento;
    }
}
