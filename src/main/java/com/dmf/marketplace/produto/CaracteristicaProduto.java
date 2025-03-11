package com.dmf.marketplace.produto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "tb_produto_caracteristica")
public class CaracteristicaProduto {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Size(min = 3)
    private String nome;

    @NotBlank
    @Size(min = 3)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public CaracteristicaProduto(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    @Deprecated
    public CaracteristicaProduto() {
    }

    // Método para associar o Produto depois
    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Produto getProduto() {
        return produto;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CaracteristicaProduto that = (CaracteristicaProduto) o;
        return Objects.equals(getNome(), that.getNome()) && Objects.equals(getProduto(), that.getProduto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNome(), getProduto());
    }
}
