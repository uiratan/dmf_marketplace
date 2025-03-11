package com.dmf.marketplace.produto.dto;

import com.dmf.marketplace.produto.CaracteristicaProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NovaCaracteristicaProdutoRequest {

    @NotBlank
    @Size(min = 3)
    private String nome;

    @NotBlank
    @Size(min = 3)
    private String descricao;

    public NovaCaracteristicaProdutoRequest(String caracteristica, String descricao) {
        this.nome = caracteristica;
        this.descricao = descricao;
    }

    public CaracteristicaProduto toModel() {
        return new CaracteristicaProduto(this.nome, this.descricao);
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return "NovaCaracteristicaProdutoRequest{" +
                "caracteristica='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
