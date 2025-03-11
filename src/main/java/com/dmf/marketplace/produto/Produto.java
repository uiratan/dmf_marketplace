package com.dmf.marketplace.produto;

import com.dmf.marketplace.categoria.Categoria;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id
    @GeneratedValue
    private Long id;

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
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL) // Cascade para persistir as características
    private List<CaracteristicaProduto> caracteristicas;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String descricao;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Produto(
            final String nome,
            final BigDecimal valor,
            final int quantidadeEstoque,
            final List<CaracteristicaProduto> caracteristicas,
            final String descricao,
            final Categoria categoria,
            final Usuario usuario) {
        Assert.hasText(nome, "Nome não pode ser nulo ou vazio");
        Assert.notNull(valor, "Valor não pode ser nulo");
        Assert.isTrue(valor.compareTo(BigDecimal.ZERO) > 0, "Valor deve ser maior que zero");
        Assert.isTrue(quantidadeEstoque >= 0, "Quantidade em estoque não pode ser negativa");
        Assert.notNull(caracteristicas, "Lista de características não pode ser nula");
        Assert.isTrue(!caracteristicas.isEmpty(), "Produto deve ter ao menos uma característica");
        Assert.hasText(descricao, "Descrição não pode ser nula ou vazia");
        Assert.notNull(categoria, "Categoria não pode ser nula");
        Assert.notNull(usuario, "Usuário não pode ser nulo");

        this.nome = nome;
        this.valor = valor;
        this.quantidadeEstoque = quantidadeEstoque;
        this.caracteristicas = caracteristicas;
        this.descricao = descricao;
        this.categoria = categoria;
        this.usuario = usuario;
        associaCaracteristicas(); // Associa o Produto às características
    }

    @Deprecated
    public Produto() {
    }

    // Método para associar o Produto às características
    private void associaCaracteristicas() {
        if (this.caracteristicas != null) {
            for (CaracteristicaProduto caracteristica : this.caracteristicas) {
                caracteristica.setProduto(this);
            }
        }
    }

    public Long getId() {
        return id;
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

    public List<CaracteristicaProduto> getCaracteristicas() {
        return caracteristicas;
    }

    public String getDescricao() {
        return descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
