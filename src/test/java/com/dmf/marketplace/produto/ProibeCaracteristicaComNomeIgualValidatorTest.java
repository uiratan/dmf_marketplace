package com.dmf.marketplace.produto;

import com.dmf.marketplace.produto.dto.NovaCaracteristicaProdutoRequest;
import com.dmf.marketplace.produto.dto.NovoProdutoRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

class ProibeCaracteristicaComNomeIgualValidatorTest {


    private static Stream<Arguments> provideValidationScenarios() {
        return Stream.of(
                Arguments.of(
                        true,
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Tela", "AMOLED 6.5 polegadas"),
                                new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                                new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                                new NovaCaracteristicaProdutoRequest("Bateria", "108MP com modo noturno"))),
                Arguments.of(
                        false,
                        Set.of(
                                new NovaCaracteristicaProdutoRequest("Tela", "AMOLED 6.5 polegadas"),
                                new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                                new NovaCaracteristicaProdutoRequest("Bateria", "108MP com modo noturno"))),
                Arguments.of(
                        false,
                        new HashSet<>())
        );
    }

    @Test
    void deveIgnorarValidacaoSeJaExistiremErros() {
        NovoProdutoRequest request = new NovoProdutoRequest(
                "Produto",                  // Nome válido
                BigDecimal.valueOf(100.00), // Valor válido
                10,                         // Quantidade válida
                new HashSet<>(),            // Características vazias (válido)
                "Descrição válida aqui",    // Descrição válida
                1L                          // ID de categoria válido
        );
        ProibeCaracteristicaComNomeIgualValidator validator = new ProibeCaracteristicaComNomeIgualValidator();
        Errors errors = new BeanPropertyBindingResult(request, "teste");
        errors.rejectValue("nome", null, "erro no nome");

        validator.validate(request, errors);

        Assertions.assertFalse(errors.hasFieldErrors("caracteristicas"));
    }

    @ParameterizedTest
    @MethodSource("provideValidationScenarios")
    void deveRejeitarCaracteristicasDuplicadas(
            boolean esperado,
            Set<NovaCaracteristicaProdutoRequest> caracteristicas
    ) {
        NovoProdutoRequest request = new NovoProdutoRequest(
                "Produto B",
                new BigDecimal("150.0"),
                5,
                caracteristicas,
                "Descrição válida para o produto",
                2L
        );

        ProibeCaracteristicaComNomeIgualValidator validator = new ProibeCaracteristicaComNomeIgualValidator();
        Errors errors = new BeanPropertyBindingResult(request, "teste");

        validator.validate(request, errors);

        Assertions.assertEquals(esperado, errors.hasFieldErrors("caracteristicas"));

    }

}