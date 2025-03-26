package com.dmf.marketplace.produto;

import com.dmf.marketplace.categoria.Categoria;
import com.dmf.marketplace.usuario.SenhaLimpa;
import com.dmf.marketplace.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoTest {

    private Categoria categoria;
    private Usuario usuario;
    private Set<CaracteristicaProduto> caracteristicas = new HashSet<>();

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Eletrônicos");
        usuario = new Usuario("usuario@email.com", new SenhaLimpa("senhaSegura"));
        caracteristicas = Set.of(
                new CaracteristicaProduto("Tela", "OLED 6.5"),
                new CaracteristicaProduto("Bateria", "5000mAh"),
                new CaracteristicaProduto("Processador", "Snapdragon 8 Gen 2")
        );
    }

    @Test
    void deveCriarProdutoComSucesso() {
        Produto produto = new Produto("Smartphone",
                new BigDecimal("2500"),
                10,
                caracteristicas,
                "Smartphone avançado com tela OLED e bateria de longa duração",
                categoria,
                usuario);

        assertThat(produto).isNotNull();
        assertThat(produto.getNome()).isEqualTo("Smartphone");
        assertThat(produto.getValor()).isEqualByComparingTo("2500");
        assertThat(produto.getQuantidadeEstoque()).isEqualTo(10);
        assertThat(produto.getCaracteristicas()).hasSize(3);
        assertThat(produto.getCategoria()).isEqualTo(categoria);
        assertThat(produto.getDono()).isEqualTo(usuario);
    }

    @Test
    void naoDeveCriarProdutoComNomeVazio() {
        Executable executable = () -> new Produto("", new BigDecimal("2500"), 10, caracteristicas, "Descrição", categoria, usuario);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void naoDeveCriarProdutoComValorNegativo() {
        Executable executable = () -> new Produto("Smartphone", new BigDecimal("-100"), 10, caracteristicas, "Descrição", categoria, usuario);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void naoDeveCriarProdutoComEstoqueNegativo() {
        Executable executable = () -> new Produto("Smartphone", new BigDecimal("2500"), -5, caracteristicas, "Descrição", categoria, usuario);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void naoDeveCriarProdutoComMenosDeTresCaracteristicas() {
        Set<CaracteristicaProduto> poucasCaracteristicas = Set.of(new CaracteristicaProduto("Tela", "OLED"));
        Executable executable = () -> new Produto("Smartphone", new BigDecimal("2500"), 10, poucasCaracteristicas, "Descrição", categoria, usuario);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void produtosComMesmoIdDevemSerIguais() {
        Produto produto1 = new Produto("Smartphone", new BigDecimal("2500"), 10, caracteristicas, "Descrição", categoria, usuario);
        Produto produto2 = new Produto("Smartphone", new BigDecimal("2500"), 10, caracteristicas, "Descrição", categoria, usuario);

        assertEquals(produto1, produto2);
        assertThat(produto1).isEqualTo(produto2);
    }
}
