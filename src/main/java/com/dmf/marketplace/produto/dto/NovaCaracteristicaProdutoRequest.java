package com.dmf.marketplace.produto.dto;

import com.dmf.marketplace.produto.CaracteristicaProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NovaCaracteristicaProdutoRequest {

    @NotBlank
    @Size(min = 3)
    private final String nome;

    @NotBlank
    @Size(min = 3)
    private final String descricao;

    public NovaCaracteristicaProdutoRequest(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public CaracteristicaProduto toModel() {
        return new CaracteristicaProduto(this.nome, this.descricao);
    }

    @Override
    public String toString() {
        return "NovaCaracteristicaProdutoRequest{" +
                "nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
