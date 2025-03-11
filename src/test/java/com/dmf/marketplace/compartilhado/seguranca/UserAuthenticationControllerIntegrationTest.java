package com.dmf.marketplace.compartilhado.seguranca;

import com.dmf.marketplace.usuario.SenhaLimpa;
import com.dmf.marketplace.usuario.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @PersistenceContext
    private EntityManager manager;

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
        // Inserir usuário no banco
        String email = "test@example.com";
        String rawPassword = "password";

        Usuario usuario = new Usuario(email, new SenhaLimpa(rawPassword));
        manager.persist(usuario);
        manager.flush();

        LoginInputDto loginInfo = new LoginInputDto();
        loginInfo.setEmail(email);
        loginInfo.setPassword(rawPassword);

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInfo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}