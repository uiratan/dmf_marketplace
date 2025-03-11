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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NovoProdutoRequest {
    @NotBlank
    @Size(min = 3)
    private final String nome;

    @NotNull
    @Positive
    private final BigDecimal valor;

    @Min(0)
    private final int quantidadeEstoque;

    @Size(min = 3)
    @Valid
    private final Set<NovaCaracteristicaProdutoRequest> caracteristicas = new HashSet<>();

    @NotBlank
    @Size(min = 10, max = 1000)
    private final String descricao;

    @NotNull
    @ExistsId(domainClass = Categoria.class, fieldName = "id")
    private final Long idCategoria;

    public NovoProdutoRequest(
            final String nome,
            final BigDecimal valor,
            final int quantidadeEstoque,
            final Set<NovaCaracteristicaProdutoRequest> caracteristicas,
            final String descricao,
            final Long idCategoria) {
        this.nome = nome;
        this.valor = valor;
        this.quantidadeEstoque = quantidadeEstoque;
        this.caracteristicas.addAll(caracteristicas);
        this.descricao = descricao;
        this.idCategoria = idCategoria;
    }

    public Produto toModel(
            EntityManager manager,
            Usuario usuario) {
        Assert.notNull(usuario, "usuario não pode ser nulo");

        Categoria categoria = manager.find(Categoria.class, idCategoria);
        Assert.notNull(categoria, "categoria não existe no banco");

        Set<CaracteristicaProduto> caracteristicaProdutoList =
                this.caracteristicas.stream()
                        .map(NovaCaracteristicaProdutoRequest::toModel)
                        .collect(Collectors.toSet());

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

    public Set<NovaCaracteristicaProdutoRequest> getCaracteristicas() {
        return caracteristicas;
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

    public Set<String> buscarCaracteristicasIguais() {
        HashSet<String> nomesIguais = new HashSet<>();
        HashSet<String> resultados = new HashSet<>();
        for (NovaCaracteristicaProdutoRequest caracteristica : this.caracteristicas) {
            String nome = caracteristica.getNome();
            if (!nomesIguais.add(nome)) {
                resultados.add(nome);
            }
        }
        return resultados;
    }
}
