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

    public NovoProdutoRequest(String nome, BigDecimal valor, int quantidadeEstoque, List<NovaCaracteristicaProdutoRequest> caracteristicas, String descricao, Long idCategoria) {
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
        Usuario usuario = manager.find(Usuario.class, idUsuario);

        Assert.notNull(categoria, "categoria não existe no banco");
        Assert.notNull(usuario, "usuario não existe no banco");

        List<CaracteristicaProduto> caracteristicaProdutoList
                = this.caracteristicas.stream()
                .map(c -> new CaracteristicaProduto(c.getCaracteristica(), c.getDescricao()))
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

    public String getNome() {
        return nome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public List<NovaCaracteristicaProdutoRequest> getCaracteristicas() {
        return caracteristicas;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getIdCategoria() {
        return idCategoria;
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
