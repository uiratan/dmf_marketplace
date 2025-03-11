package com.dmf.marketplace.produto;

import com.dmf.marketplace.produto.dto.NovaCaracteristicaProdutoRequest;
import com.dmf.marketplace.produto.dto.NovoProdutoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.Mockito.*;

class ProibeCaracteristicaComNomeIgualValidatorTest {

    private ProibeCaracteristicaComNomeIgualValidator validator;
    private Errors mockErrors;

    @BeforeEach
    void setup() {
        validator = new ProibeCaracteristicaComNomeIgualValidator();
        mockErrors = mock(Errors.class);
    }

    @Test
    void deveIgnorarValidacaoSeJaExistiremErros() {
        NovoProdutoRequest request = mock(NovoProdutoRequest.class);
        when(mockErrors.hasErrors()).thenReturn(true);

        validator.validate(request, mockErrors);

        verify(mockErrors, never()).rejectValue(any(), any(), any());
    }

    @Test
    void devePassarSemErrosQuandoNaoHaCaracteristicasDuplicadas() {
        NovoProdutoRequest target = new NovoProdutoRequest(
                "Produto A",
                new BigDecimal("100.0"),
                10,
                Set.of(
                        new NovaCaracteristicaProdutoRequest("Tela", "AMOLED 6.5 polegadas"),
                        new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                        new NovaCaracteristicaProdutoRequest("Bateria", "108MP com modo noturno")
                ),
                "Descrição válida para o produto",
                1L
        );

        when(mockErrors.hasErrors()).thenReturn(false);

        validator.validate(target, mockErrors);

        verify(mockErrors, never()).rejectValue(eq("caracteristicas"), any(), any());
    }

    @Test
    void deveRejeitarCaracteristicasDuplicadas() {
        NovoProdutoRequest request = new NovoProdutoRequest(
                "Produto B",
                new BigDecimal("150.0"),
                5,
                Set.of(
                        new NovaCaracteristicaProdutoRequest("Tela", "AMOLED 6.5 polegadas"),
                        new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                        new NovaCaracteristicaProdutoRequest("Camera", "5000mAh com carregamento rápido"),
                        new NovaCaracteristicaProdutoRequest("Bateria", "108MP com modo noturno")
                ),
                "Descrição válida para o produto",
                2L
        );

        when(mockErrors.hasErrors()).thenReturn(false);

        validator.validate(request, mockErrors);

        verify(mockErrors).rejectValue(eq("caracteristicas"), isNull(), contains("não é possível adicionar caracteristicas iguais"));
    }



}