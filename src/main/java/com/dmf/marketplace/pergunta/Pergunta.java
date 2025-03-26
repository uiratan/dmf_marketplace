package com.dmf.marketplace.pergunta;

import com.dmf.marketplace.compartilhado.ExcludeFromJacocoGeneratedReport;
import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_pergunta")
public class Pergunta implements Comparable<Pergunta> {

    @Id @GeneratedValue private Long id;

    @NotBlank String titulo;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "consumidor_id")
    @NotNull
    Usuario interessado;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "produto_id")
    @NotNull
    Produto produto;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    public Pergunta(String titulo, Usuario interessado, Produto produto) {
        this.titulo = titulo;
        this.interessado = interessado;
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

    public Usuario getInteressado() {
        return interessado;
    }

    public Produto getProduto() {
        return produto;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pergunta pergunta = (Pergunta) o;
        return Objects.equals(getTitulo(), pergunta.getTitulo()) && Objects.equals(getInteressado(), pergunta.getInteressado());
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public int hashCode() {
        return Objects.hash(getTitulo(), getInteressado());
    }

    @Override
    public int compareTo(Pergunta o) {
        return this.titulo.compareTo(o.titulo);
    }
}
