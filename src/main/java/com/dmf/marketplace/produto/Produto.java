package com.dmf.marketplace.produto;

import com.dmf.marketplace.categoria.Categoria;
import com.dmf.marketplace.compartilhado.ExcludeFromJacocoGeneratedReport;
import com.dmf.marketplace.opiniao.Opiniao;
import com.dmf.marketplace.pergunta.Pergunta;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id @GeneratedValue private Long id;
    @NotBlank @Size(min = 3) private String nome;
    @NotNull @Positive private BigDecimal valor;
    @Min(0) private int quantidadeEstoque;
    @NotBlank @Size(min = 10, max = 1000)
    private String descricao;

    //1
    @JsonManagedReference
    @Size(min = 3) @Valid
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL) // Cascade para persistir as características
    private final Set<CaracteristicaProduto> caracteristicas = new HashSet<>();

    //1
    @NotNull
    @ManyToOne @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    //1
    @ManyToOne @JoinColumn(name = "usuario_id")
    private Usuario dono;

    @ElementCollection // Para uma tabela separada de imagens
    @Column(name = "imagem_url")
    private List<String> imagens = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Opiniao> opinioes = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "produto")
    private SortedSet<Pergunta> perguntas = new TreeSet<>();

    public Produto(
            final String nome,
            final BigDecimal valor,
            final int quantidadeEstoque,
            final Set<CaracteristicaProduto> caracteristicas,
            final String descricao,
            final Categoria categoria,
            final Usuario dono) {
        Assert.hasText(nome, "Nome não pode ser nulo ou vazio");
        Assert.notNull(valor, "Valor não pode ser nulo");
        Assert.isTrue(valor.compareTo(BigDecimal.ZERO) > 0, "Valor deve ser maior que zero");
        Assert.isTrue(quantidadeEstoque >= 0, "Quantidade em estoque não pode ser negativa");
        Assert.isTrue(caracteristicas.size() >= 3, "Devem haver no mínimo 3 características diferentes para o produto");
        Assert.notNull(caracteristicas, "Lista de características não pode ser nula");
        Assert.hasText(descricao, "Descrição não pode ser nula ou vazia");
        Assert.notNull(categoria, "Categoria não pode ser nula");
        Assert.notNull(dono, "Usuário não pode ser nulo");

        this.nome = nome;
        this.valor = valor;
        this.quantidadeEstoque = quantidadeEstoque;
        this.descricao = descricao;
        this.categoria = categoria;
        this.dono = dono;
        this.caracteristicas.addAll(caracteristicas);
        associaCaracteristicas(); // Associa o Produto às características
    }

    @Deprecated
    public Produto() {
    }

    public boolean pertenceAoUsuario(Usuario usuario) {
        return this.dono.equals(usuario);
    }

    private void associaCaracteristicas() {
        for (CaracteristicaProduto caracteristica : this.caracteristicas) {
            caracteristica.setProduto(this);
        }
    }

    public void adicionarImagens(List<String> novasImagens) {
        this.imagens.addAll(novasImagens);
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

    public Set<CaracteristicaProduto> getCaracteristicas() {
        return Set.copyOf(caracteristicas);
    }

    public String getDescricao() {
        return descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<String> getImagens() {
        return List.copyOf(imagens);
    }

    public List<Opiniao> getOpinioes() {
        return List.copyOf(opinioes);
    }

    public double getMediaNotas() {
        return opinioes.stream().mapToLong(Opiniao::getNota).average().orElse(0.0);
    }

    public SortedSet<Pergunta> getPerguntas() {
        return Collections.unmodifiableSortedSet(perguntas);
    }

    @ExcludeFromJacocoGeneratedReport
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

    public Usuario getDono() {
        return dono;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", descricao='" + descricao + '\'' +
                ", caracteristicas=" + caracteristicas +
                ", categoria=" + categoria +
                ", usuario=" + dono +
                ", imagens=" + imagens +
                '}';
    }


}
