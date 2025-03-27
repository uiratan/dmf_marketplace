package com.dmf.marketplace.compra;

public class NovaCompraResponse {

    private final String id;
    private final String status;
    private final String gatewayPagamento;
    private final String produto;
    private final Integer quantidade;
    private final String comprador;

    public NovaCompraResponse(Compra compra) {
        this.id = compra.getId().toString();
        this.status = compra.getStatus().name();
        this.gatewayPagamento = compra.getGatewayPagamento().name();
        this.produto = compra.getProduto().getNome();
        this.quantidade = compra.getQuantidade();
        this.comprador = compra.getComprador().getLogin();
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getGatewayPagamento() {
        return gatewayPagamento;
    }

    public String getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public String getComprador() {
        return comprador;
    }
}
