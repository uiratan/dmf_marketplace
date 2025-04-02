package com.dmf.marketplace.compra;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_transacao")
public class Transacao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String idTransacaoGateway;
    @Enumerated(EnumType.STRING) @NotNull private StatusTransacao statusTransacao;
    @NotNull private final LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    public Transacao(
            String idTransacaoGateway,
            StatusTransacao statusTransacao,
            Compra compra) {
        this.idTransacaoGateway = idTransacaoGateway;
        this.statusTransacao = statusTransacao;
        this.compra = compra;
    }

    @Deprecated
    public Transacao() {}

    public boolean concluidaComSucesso() {
        return this.statusTransacao == StatusTransacao.SUCESSO;
    }

    public String getIdTransacaoGateway() {
        return idTransacaoGateway;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(idTransacaoGateway, transacao.idTransacaoGateway);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idTransacaoGateway);
    }

    @Override
    public String toString() {
        return "Transacao{" +
               "idTransacao='" + idTransacaoGateway + '\'' +
               ", statusTransacao=" + statusTransacao +
               '}';
    }
}
