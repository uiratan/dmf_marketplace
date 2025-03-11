package com.dmf.marketplace.produto;

import com.dmf.marketplace.produto.dto.NovoProdutoRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Set;

public class ProibeCaracteristicaComNomeIgualValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return NovoProdutoRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }

        NovoProdutoRequest request = (NovoProdutoRequest) target;
        Set<String> nomesIguais = request.buscarCaracteristicasIguais();
        if (!nomesIguais.isEmpty()) {
            errors.rejectValue("caracteristicas", null,
                    "não é possível adicionar caracteristicas iguais " +
                    nomesIguais);
        }
    }
}
