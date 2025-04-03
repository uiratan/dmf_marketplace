package com.dmf.marketplace.compra;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_compra")
public class Compra {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
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

    @Enumerated(EnumType.STRING)
    private StatusCompra status;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.MERGE)
    private Set<Transacao> transacoes = new HashSet<>();


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

    public void adicionaTransacao(@Valid RetornoGatewayPagamento request) {
        Transacao novaTransacao = request.toTransacao(this);
        Assert.isTrue(!transacaoJaProcessada(novaTransacao), "Esta transação já foi processada: " + novaTransacao.getIdTransacaoGateway());

        Assert.isTrue(!processadaComSucesso(), "Já existe uma transação bem-sucedida para esta compra.");

        this.transacoes.add(novaTransacao);
    }

    private boolean transacaoJaProcessada(Transacao novaTransacao) {
        return this.transacoes.contains(novaTransacao);
    }

    boolean processadaComSucesso() {
        return !transacoesConcluidasComSucesso().isEmpty();
    }

    private List<Transacao> transacoesConcluidasComSucesso() {
        List<Transacao> transacoesConcluidasComSucesso = this.transacoes.stream().filter(Transacao::concluidaComSucesso).toList();
        Assert.isTrue(transacoesConcluidasComSucesso.size() <= 1, "Mais de uma transação bem-sucedida para esta compra.");
        return transacoesConcluidasComSucesso;
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

    @Override
    public String toString() {
        return "Compra{" +
               "id=" + id +
               ", gatewayPagamento=" + gatewayPagamento +
               ", produto=" + produto +
               ", quantidade=" + quantidade +
               ", comprador=" + comprador +
               ", status=" + status +
               ", transacoes=" + transacoes +
               '}';
    }
}
