package com.dmf.marketplace.categoria;

import com.dmf.marketplace.BaseAuthenticatedControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoriaControllerTest extends BaseAuthenticatedControllerTest {

    private static final String BASE_URL = "/categorias";

    @Test
    @DisplayName("deve criar uma categoria sem categoria mae")
    @Transactional
    public void teste1() throws Exception {
        NovaCategoriaRequest request = new NovaCategoriaRequest("categoria", null);
        requestAuthenticatedHelper.performPost(BASE_URL, request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve criar uma categoria com categoria mae")
    @Transactional
    public void teste2() throws Exception {
        NovaCategoriaRequest requestMae = new NovaCategoriaRequest("categoria mae", null);
        requestAuthenticatedHelper.performPost(BASE_URL, requestMae)
                .andExpect(status().isOk());

        NovaCategoriaRequest requestFilha = new NovaCategoriaRequest("categoria", requestMae.getIdCategoriaMae());
        requestAuthenticatedHelper.performPost(BASE_URL, requestFilha)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve retornar erro com nome de categoria duplicada")
    @Transactional
    public void deveriaRetornarErroComNomeDuplicado() throws Exception {
        NovaCategoriaRequest request = new NovaCategoriaRequest("categoria", null);
        requestAuthenticatedHelper.performPost(BASE_URL, request)
                .andExpect(status().isOk());

        NovaCategoriaRequest requestDuplicado = new NovaCategoriaRequest("categoria", null);
        requestAuthenticatedHelper.performPost(BASE_URL, requestDuplicado)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve retornar erro com id de categoria inexistente")
    @Transactional
    public void teste4() throws Exception {
        NovaCategoriaRequest request = new NovaCategoriaRequest("categoria", 1L);
        requestAuthenticatedHelper.performPost(BASE_URL, request)
                .andExpect(status().isBadRequest());
    }
}