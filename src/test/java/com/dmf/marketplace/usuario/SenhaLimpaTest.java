package com.dmf.marketplace.usuario;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SenhaLimpaTest {

    @DisplayName("só aceita senha com 6 ou mais caracteres")
    @ParameterizedTest
    @CsvSource({
            "123456",
            "1234567",
            "1234569879456465",
    })
    void teste1(String senhaLimpa) {
        new SenhaLimpa(senhaLimpa);
    }


    @DisplayName("nao aceita senha com 5 ou menos caracteres")
    @ParameterizedTest
    @CsvSource({
            "12345",
            "1234",
            "123",
    })
    void teste2(String senhaLimpa) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SenhaLimpa(senhaLimpa));
    }
}