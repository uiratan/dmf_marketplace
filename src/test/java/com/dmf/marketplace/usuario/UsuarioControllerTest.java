package com.dmf.marketplace.usuario;

import com.dmf.marketplace.BaseAuthenticatedControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UsuarioControllerTest extends BaseAuthenticatedControllerTest {

    private static final String BASE_URL = "/usuarios";

    @Test
    @Transactional
    public void deveriaCriarUsuarioComDadosValidos() throws Exception {
        NovoUsuarioRequest request = new NovoUsuarioRequest("email@novo.com", "senha123");

        requestHelper.performPost(BASE_URL, request)
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deveriaRetornarErroComEmailDuplicado() throws Exception {
        NovoUsuarioRequest request1 = new NovoUsuarioRequest("email@duplicado.com", "senha123");
        NovoUsuarioRequest requestDuplicado = new NovoUsuarioRequest("email@duplicado.com", "senha456");

        requestHelper.performPost(BASE_URL, request1)
                .andExpect(status().isOk());

        requestHelper.performPost(BASE_URL, requestDuplicado)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void deveriaRetornarUsuarioPorId() throws Exception {
        Usuario usuario = new Usuario("usuario@gmail.com", new SenhaLimpa("123456"));
        entityManager.persist(usuario);
        entityManager.flush();

        requestHelper.performGet(BASE_URL + "/" + usuario.getId())
                .andExpect(status().isOk())
        .andExpect(content().string(containsString("usuario@gmail.com")));
    }

    @Test
    @Transactional
    public void deveriaRetornar404QuandoIdNaoExiste() throws Exception {
        requestHelper.performGet(BASE_URL + "/999")
                .andExpect(status().isNotFound());
    }
}