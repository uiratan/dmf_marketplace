package com.dmf.marketplace.opiniao;

import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_opiniao")
public class Opiniao {

    @Id @GeneratedValue private Long id;
    @Min(1) @Max(5) int nota;
    @NotBlank String titulo;
    @NotBlank String descricao;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "consumidor_id")
    @NotNull Usuario consumidor;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "produto_id")
    @NotNull Produto produto;

    public Opiniao(int nota, String titulo, String descricao, Usuario consumidor, Produto produto) {
        this.nota = nota;
        this.titulo = titulo;
        this.descricao = descricao;
        this.consumidor = consumidor;
        this.produto = produto;
    }

    @Deprecated
    public Opiniao() {}

    public int getNota() {
        return nota;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getConsumidor() {
        return consumidor.getLogin();
    }

    public String getProduto() {
        return produto.getNome();
    }
}
