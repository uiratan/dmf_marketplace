package com.dmf.marketplace;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;

/**
 * Classe base para testes de controladores que requerem autenticação, fornecendo um "token" JWT.
 */
public abstract class BaseAuthenticatedControllerTest extends BaseControllerTest {

    @PersistenceContext protected EntityManager entityManager;
    protected String token;

    @Override
    @BeforeEach
    @Transactional
    public void setUp() throws Exception {
        super.setUp();
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        token = AuthenticationHelper.generateToken(mockMvc, objectMapper, entityManager, uniqueEmail, "password");
        requestHelper.withHeader("Authorization", "Bearer " + token);
    }
}