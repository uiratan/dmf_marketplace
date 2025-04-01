package com.dmf.marketplace.compra;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Entity
@Table(name = "tb_compra")
public class Compra {

    @Id
    private UUID id;

    private GatewayPagamento gatewayPagamento;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Positive
    private Integer quantidade;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Usuario comprador;

    private StatusCompra status;

    public Compra(
            Produto produto,
            @NotNull @Positive Integer quantidade,
            Usuario comprador,
            GatewayPagamento gatewayPagamento
    ) {

        this.id = UUID.randomUUID();
        this.gatewayPagamento = gatewayPagamento;
        this.produto = produto;
        this.quantidade = quantidade;
        this.comprador = comprador;
        this.status = StatusCompra.INICIADA;
    }

    @Deprecated
    public Compra() {
    }

    public String urlRedirecionamento(UriComponentsBuilder uriComponentsBuilder) {
        return this.gatewayPagamento.criaUrlRetorno(this, uriComponentsBuilder);
    }


    public UUID getId() {
        return id;
    }

    public GatewayPagamento getGatewayPagamento() {
        return gatewayPagamento;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public StatusCompra getStatus() {
        return status;
    }
}
