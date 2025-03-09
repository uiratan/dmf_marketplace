package com.dmf.marketplace.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para serializar/deserializar JSON

    @Test
    public void deveriaCriarUsuarioComDadosValidos() throws Exception {
        // Arrange: cria um request válido
        NovoUsuarioRequest request = new NovoUsuarioRequest("email@novo.com", "senha123");
        String jsonRequest = objectMapper.writeValueAsString(request);

        // Act & Assert: faz a requisição POST e verifica o status
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk()); // Como não retorna nada, esperamos 200 OK
    }

    @Test
    public void deveriaRetornarErroComEmailDuplicado() throws Exception {
        // Arrange: cria um usuário com email duplicado
        NovoUsuarioRequest request1 = new NovoUsuarioRequest("email@duplicado.com", "senha123");
        NovoUsuarioRequest request2 = new NovoUsuarioRequest("email@duplicado.com", "senha456");
        String jsonRequest1 = objectMapper.writeValueAsString(request1);
        String jsonRequest2 = objectMapper.writeValueAsString(request2);

        // Act: cria o primeiro usuário
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1))
                .andExpect(status().isOk());

        // Act & Assert: tenta criar o segundo com email duplicado
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest2))
                .andExpect(status().isBadRequest()); // Espera 400 por validação
    }

    @Test
    public void deveriaRetornarUsuarioPorId() throws Exception {
        // Arrange: cria um usuário primeiro
        NovoUsuarioRequest request = new NovoUsuarioRequest("email@unico.com",  "senha123");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        // Act & Assert: busca o usuário pelo ID (assumindo ID 1 para o primeiro)
        mockMvc.perform(get("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("email@unico.com")));
    }

    @Test
    public void deveriaRetornar404QuandoIdNaoExiste() throws Exception {
        // Act & Assert: tenta buscar um ID inexistente
        mockMvc.perform(get("/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera 404
    }
}