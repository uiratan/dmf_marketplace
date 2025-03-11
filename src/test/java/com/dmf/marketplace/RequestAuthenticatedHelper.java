package com.dmf.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

public class RequestAuthenticatedHelper extends RequestHelper {

    public RequestAuthenticatedHelper(MockMvc mockMvc, ObjectMapper objectMapper, String token) {
        super(mockMvc, objectMapper);
        this.defaultHeaders = new HashMap<>();
        this.defaultHeaders.put("Authorization", "Bearer " + token);
    }

}