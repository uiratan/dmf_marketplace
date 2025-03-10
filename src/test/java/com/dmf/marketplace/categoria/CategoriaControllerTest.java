package com.dmf.marketplace.categoria;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para serializar/deserializar JSON

    @PersistenceContext
    private EntityManager manager;

    @Test
    @DisplayName("deve criar uma categoria sem categoria mae")
    public void teste1() throws Exception {
        // Arrange: cria um request válido
        NovaCategoriaRequest request = new NovaCategoriaRequest("categoria", null);
        String jsonRequest = objectMapper.writeValueAsString(request);

        // Act & Assert: faz a requisição POST e verifica o status
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk()); // Como não retorna nada, esperamos 200 OK
    }

    @Test
    @DisplayName("deve criar uma categoria com categoria mae")
    public void teste2() throws Exception {
        // Arrange
        Categoria categoriaMae = new Categoria("categoria mae");
        manager.persist(categoriaMae);
        manager.flush();

        NovaCategoriaRequest requestFilha = new NovaCategoriaRequest("categoria", categoriaMae.getId());
        String jsonRequestFilha = objectMapper.writeValueAsString(requestFilha);

        // Act & Assert: faz a requisição POST e verifica o status
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestFilha))
                .andExpect(status().isOk()); // Como não retorna nada, esperamos 200 OK
    }

    @Test
    @DisplayName("deve retornar erro com nome de categoria duplicada")
    public void deveriaRetornarErroComEmailDuplicado() throws Exception {
        // Arrange: cria um usuário com email duplicado
        NovaCategoriaRequest request1 = new NovaCategoriaRequest("categoria", null);
        NovaCategoriaRequest request2 = new NovaCategoriaRequest("categoria", null);
        String jsonRequest1 = objectMapper.writeValueAsString(request1);
        String jsonRequest2 = objectMapper.writeValueAsString(request2);

        // Act: cria o primeiro usuário
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1))
                .andExpect(status().isOk());

        // Act & Assert: tenta criar o segundo com email duplicado
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest2))
                .andExpect(status().isBadRequest()); // Espera 400 por validação
    }

    @Test
    @DisplayName("deve retornar erro com id de categoria inexistente")
    public void teste4() throws Exception {
        // Arrange: cria um usuário com email duplicado
        NovaCategoriaRequest request1 = new NovaCategoriaRequest("categoria", 1L);
        String jsonRequest1 = objectMapper.writeValueAsString(request1);

        // Act & Assert: tenta criar o segundo com email duplicado
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1))
                .andExpect(status().isBadRequest()); // Espera 400 por validação
    }
}