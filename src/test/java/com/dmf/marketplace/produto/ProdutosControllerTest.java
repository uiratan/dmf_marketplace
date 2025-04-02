package com.dmf.marketplace.produto;

import com.dmf.marketplace.BaseAuthenticatedControllerTest;
import com.dmf.marketplace.categoria.Categoria;
import com.dmf.marketplace.produto.dto.NovaCaracteristicaProdutoRequest;
import com.dmf.marketplace.produto.dto.NovoProdutoRequest;
import com.dmf.marketplace.produto.dto.NovoProdutoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProdutosControllerTest extends BaseAuthenticatedControllerTest {

    private static final String BASE_URL = "/produtos";

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria("Eletrônicos");
        entityManager.persist(categoria);
        return categoria;
    }

    private Set<NovaCaracteristicaProdutoRequest> criarCaracteristicas() {
        return Set.of(
                new NovaCaracteristicaProdutoRequest("Tela", "AMOLED 6.5 polegadas"),
                new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                new NovaCaracteristicaProdutoRequest("Bateria", "108MP com modo noturno")
        );
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso")
    @Transactional
    public void deveCriarProdutoComSucesso() throws Exception {
        Categoria categoria = criarCategoria();
        Set<NovaCaracteristicaProdutoRequest> caracteristicas = criarCaracteristicas();

        NovoProdutoRequest request = new NovoProdutoRequest(
                "Produto B",
                new BigDecimal("150.0"),
                5,
                caracteristicas,
                "Descrição válida para o produto",
                categoria.getId()
        );

        MvcResult result = requestHelper.performPost(BASE_URL, request)
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        assertThat(responseJson).isNotEmpty();

        NovoProdutoResponse produtoResponse = objectMapper.readValue(responseJson, NovoProdutoResponse.class);
        assertThat(produtoResponse.nome()).isEqualTo("Produto B");
        assertThat(produtoResponse.valor()).isEqualTo(new BigDecimal("150.0"));
        assertThat(produtoResponse.quantidadeEstoque()).isEqualTo(5);
        assertThat(produtoResponse.descricao()).isEqualTo("Descrição válida para o produto");
        assertThat(produtoResponse.caracteristicas()).hasSize(3);
        assertThat(produtoResponse.idCategoria()).isEqualTo(categoria.getId());

        Produto produtoPersistido = entityManager.createQuery("SELECT p FROM Produto p WHERE p.nome = :nome", Produto.class)
                .setParameter("nome", "Produto B")
                .getSingleResult();
        assertThat(produtoPersistido).isNotNull();
        assertThat(produtoPersistido.getNome()).isEqualTo("Produto B");
        assertThat(produtoPersistido.getCaracteristicas()).hasSize(3);
        assertThat(produtoPersistido.getCategoria().getId()).isEqualTo(categoria.getId());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando o nome do produto for nulo")
    @Transactional
    public void deveFalharQuandoNomeNulo() throws Exception {
        Categoria categoria = criarCategoria();
        Set<NovaCaracteristicaProdutoRequest> caracteristicas = criarCaracteristicas();

        NovoProdutoRequest requestInvalido = new NovoProdutoRequest(
                null,
                new BigDecimal("150.0"),
                5,
                caracteristicas,
                "Descrição válida para o produto",
                categoria.getId()
        );

        requestHelper.performPost(BASE_URL, requestInvalido)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusRetorno").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos informados"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/produtos"))
                .andExpect(jsonPath("$.errors.nome").value("must not be blank"));
    }
}