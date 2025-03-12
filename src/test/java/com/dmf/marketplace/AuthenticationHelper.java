package com.dmf.marketplace;

import com.dmf.marketplace.compartilhado.seguranca.LoginInputDto;
import com.dmf.marketplace.usuario.SenhaLimpa;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthenticationHelper {

    /**
     * Gera um token JWT autenticando um usuário no endpoint /api/auth.
     *
     * @param mockMvc       MockMvc para simular requisições HTTP
     * @param objectMapper  ObjectMapper para serializar/deserializar JSON
     * @param entityManager EntityManager para persistir o usuário
     * @param email         Email do usuário
     * @param rawPassword      Senha em texto plano
     * @return Token JWT gerado
     * @throws Exception Se a autenticação falhar
     */
    protected static String generateToken(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            EntityManager entityManager,
            String email,
            String rawPassword) throws Exception {

        entityManager.persist(new Usuario(email, new SenhaLimpa(rawPassword)));

        LoginInputDto loginInfo = new LoginInputDto();
        loginInfo.setEmail(email);
        loginInfo.setPassword(rawPassword);

        MvcResult result = mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInfo)))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

}