package com.dmf.marketplace.produto.dto;

import com.dmf.marketplace.categoria.Categoria;
import com.dmf.marketplace.compartilhado.validation.ExistsId;
import com.dmf.marketplace.produto.CaracteristicaProduto;
import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

public class NovoProdutoRequest {
    @NotBlank
    @Size(min = 3)
    private String nome;

    @NotNull
    @Positive
    private BigDecimal valor;

    @Min(0)
    private int quantidadeEstoque;

    @Size(min = 3)
    @Valid
    private List<NovaCaracteristicaProdutoRequest> caracteristicas;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String descricao;

    @NotNull
    @ExistsId(domainClass = Categoria.class, fieldName = "id")
    private Long idCategoria;

    public NovoProdutoRequest(
            final String nome,
            final BigDecimal valor,
            final int quantidadeEstoque,
            final List<NovaCaracteristicaProdutoRequest> caracteristicas,
            final String descricao,
            final Long idCategoria) {
        this.nome = nome;
        this.valor = valor;
        this.quantidadeEstoque = quantidadeEstoque;
        this.caracteristicas = caracteristicas;
        this.descricao = descricao;
        this.idCategoria = idCategoria;
    }

    public Produto toModel(
            EntityManager manager,
            Long idUsuario) {

        Categoria categoria = manager.find(Categoria.class, idCategoria);
        Assert.notNull(categoria, "categoria não existe no banco");

        Usuario usuario = manager.find(Usuario.class, idUsuario);
        Assert.notNull(usuario, "usuario não existe no banco");

        List<CaracteristicaProduto> caracteristicaProdutoList =
                this.caracteristicas.stream()
                        .map(NovaCaracteristicaProdutoRequest::toModel)
                        .toList();

        return new Produto(
                this.nome,
                this.valor,
                this.quantidadeEstoque,
                caracteristicaProdutoList,
                this.descricao,
                categoria,
                usuario
        );
    }

    @Override
    public String toString() {
        return "NovoProdutoRequest{" +
                "nome='" + nome + '\'' +
                ", valor=" + valor +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", caracteristicas=" + caracteristicas +
                ", descricao='" + descricao + '\'' +
                ", idCategoria=" + idCategoria +
                '}';
    }

}
