package com.dmf.marketplace;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseAuthenticatedControllerTest extends BaseControllerTest {

    protected RequestAuthenticatedHelper requestAuthenticatedHelper;

    protected String token;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        token = AuthenticationHelper.generateToken(mockMvc, objectMapper, entityManager);
        requestAuthenticatedHelper = new RequestAuthenticatedHelper(mockMvc, objectMapper, token);
    }
}