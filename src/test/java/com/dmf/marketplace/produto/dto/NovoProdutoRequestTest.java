package com.dmf.marketplace.produto.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NovoProdutoRequestTest {

    @ParameterizedTest
    @MethodSource("provideCaracteristicasScenarios")
    public void deveIdentificarCaracteristicasIguais(
            Set<NovaCaracteristicaProdutoRequest> caracteristicas,
            Set<String> resultadoEsperado) {
        // Arrange
        NovoProdutoRequest request = new NovoProdutoRequest(
                "Produto Teste",
                BigDecimal.valueOf(100.00),
                10,
                caracteristicas,
                "Descrição válida do produto",
                1L
        );

        // Act
        Set<String> repetidas = request.buscarCaracteristicasIguais();

        // Assert
        assertEquals(resultadoEsperado, repetidas,
                "O conjunto de características repetidas deve corresponder ao esperado");
    }

    private static Stream<Arguments> provideCaracteristicasScenarios() {
        return Stream.of(
                // Caso 1: Conjunto vazio
                Arguments.of(
                        new HashSet<NovaCaracteristicaProdutoRequest>(),
                        new HashSet<String>()
                ),
                // Caso 2: Uma característica
                Arguments.of(
                        Set.of(new NovaCaracteristicaProdutoRequest("Cor", "Azul")),
                        new HashSet<String>()
                ),
                // Caso 3: Características únicas
                Arguments.of(
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Cor", "Azul"),
                                new NovaCaracteristicaProdutoRequest("Tamanho", "M"),
                                new NovaCaracteristicaProdutoRequest("Peso", "1kg")
                        ),
                        new HashSet<String>()
                ),
                // Caso 4: Uma característica repetida
                Arguments.of(
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Cor", "Azul"),
                                new NovaCaracteristicaProdutoRequest("Cor", "Verde")
                        ),
                        Set.of("Cor")
                ),
                // Caso 5: Múltiplas características repetidas
                Arguments.of(
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Cor", "Azul"),
                                new NovaCaracteristicaProdutoRequest("Cor", "Verde"),
                                new NovaCaracteristicaProdutoRequest("Tamanho", "M"),
                                new NovaCaracteristicaProdutoRequest("Tamanho", "G")
                        ),
                        Set.of("Cor", "Tamanho")
                ),
                // Caso 6: Todas características repetidas
                Arguments.of(
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Cor", "Azul"),
                                new NovaCaracteristicaProdutoRequest("Cor", "Verde"),
                                new NovaCaracteristicaProdutoRequest("Cor", "Preto")
                        ),
                        Set.of("Cor")
                ),
                // Caso 7: Mistura de únicas e repetidas
                Arguments.of(
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Cor", "Azul"),
                                new NovaCaracteristicaProdutoRequest("Cor", "Verde"),
                                new NovaCaracteristicaProdutoRequest("Tamanho", "M"),
                                new NovaCaracteristicaProdutoRequest("Peso", "1kg")
                        ),
                        Set.of("Cor")
                )
        );
    }
}