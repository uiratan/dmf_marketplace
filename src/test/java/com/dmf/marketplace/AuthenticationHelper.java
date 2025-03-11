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

    @Transactional
    protected static String generateToken(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            EntityManager entityManager) throws Exception {
        String email = "test@example.com";
        String rawPassword = "password";

        entityManager.persist(new Usuario(email, new SenhaLimpa(rawPassword)));
        entityManager.flush();

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