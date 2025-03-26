package com.dmf.marketplace.pergunta;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "tb_pergunta")
@EntityListeners(PerguntaListener.class)
public class Pergunta {

    @Id @GeneratedValue private Long id;

    @NotBlank String titulo;
    @NotBlank String pergunta;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "consumidor_id")
    @NotNull
    Usuario consumidor;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "produto_id")
    @NotNull
    Produto produto;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    public Pergunta(String titulo, String pergunta, Usuario consumidor, Produto produto) {
        this.titulo = titulo;
        this.pergunta = pergunta;
        this.consumidor = consumidor;
        this.produto = produto;
        this.createdAt = Instant.now();
    }

    @Deprecated
    public Pergunta() {}

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPergunta() {
        return pergunta;
    }

    public Usuario getConsumidor() {
        return consumidor;
    }

    public Produto getProduto() {
        return produto;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
