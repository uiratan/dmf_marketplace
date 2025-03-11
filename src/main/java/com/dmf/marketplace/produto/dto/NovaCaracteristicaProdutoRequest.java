package com.dmf.marketplace.produto.dto;

import com.dmf.marketplace.produto.CaracteristicaProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NovaCaracteristicaProdutoRequest {

    @NotBlank
    @Size(min = 3)
    private String caracteristica;

    @NotBlank
    @Size(min = 3)
    private String descricao;

    public NovaCaracteristicaProdutoRequest(String caracteristica, String descricao) {
        this.caracteristica = caracteristica;
        this.descricao = descricao;
    }

    public CaracteristicaProduto toModel() {
        return new CaracteristicaProduto(this.caracteristica, this.descricao);
    }

    public String getCaracteristica() {
        return caracteristica;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return "NovaCaracteristicaProdutoRequest{" +
                "caracteristica='" + caracteristica + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
