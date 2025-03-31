package com.dmf.marketplace.compra;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Entity
@Table(name = "tb_compra")
public class Compra {

    public static final String URL_SUCESSO_PAGAMENTO = "http://localhost:8080/compra/sucesso/";

    @Id
    private UUID id;

    private GatewayPagamento gatewayPagamento;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;
    private Integer quantidade;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Usuario comprador;

    private StatusCompra status;


    public Compra(
            GatewayPagamento gatewayPagamento,
            Produto produto,
            @NotNull @Positive Integer quantidade,
            Usuario comprador) {

        this.id = UUID.randomUUID();

        this.gatewayPagamento = gatewayPagamento;
        this.produto = produto;
        this.quantidade = quantidade;
        this.comprador = comprador;

        this.status = StatusCompra.INICIADA;

        produto.abaterEstoque(quantidade);
    }

    @Deprecated
    public Compra() {
    }

    public String gerarUrlPagamento() {
        return gatewayPagamento.gerarUrlPagamento(id, gerarUrlRetorno());
    }

    public String gerarUrlRetorno() {
        return URL_SUCESSO_PAGAMENTO + id;
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
