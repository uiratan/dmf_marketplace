package com.dmf.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RequestHelper {

    protected final MockMvc mockMvc;
    protected final ObjectMapper objectMapper;
    protected Map<String, String> defaultHeaders;

    public RequestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.defaultHeaders = new HashMap<>();
    }

    public ResultActions performPost(String url, Object requestBody) throws Exception {
        return mockMvc.perform(post(url)
                .headers(buildHeaders())
                .content(toJson(requestBody)));
    }

    public ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .headers(buildHeaders()));
    }

    private String toJson(Object request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE); // Padrão fixo
        defaultHeaders.forEach(headers::add);
        return headers;
    }

    // Método para adicionar headers customizados, se necessário
    public void withHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }
}