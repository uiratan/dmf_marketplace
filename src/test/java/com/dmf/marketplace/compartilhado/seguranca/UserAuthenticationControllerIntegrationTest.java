package com.dmf.marketplace.compartilhado.seguranca;

import com.dmf.marketplace.usuario.SenhaLimpa;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldReturnBadRequestForNonExistentUser() throws Exception {
        LoginInputDto loginInfo = new LoginInputDto();
        loginInfo.setEmail("nonexistent@example.com");
        loginInfo.setPassword("password");

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedForProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(post("/categorias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForProtectedEndpointWithInvalidToken() throws Exception {
        mockMvc.perform(post("/categorias")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldAuthenticateSuccessfullyWithValidUser() throws Exception {
        // Inserir usuário no banco (exemplo simplificado)
        UsersService usersService = applicationContext.getBean(UsersService.class);
        EntityManager entityManager = applicationContext.getBean(EntityManager.class);
        entityManager.persist(new Usuario("test@example.com", new SenhaLimpa("password")));

        LoginInputDto loginInfo = new LoginInputDto();
        loginInfo.setEmail("test@example.com");
        loginInfo.setPassword("password");

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInfo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}