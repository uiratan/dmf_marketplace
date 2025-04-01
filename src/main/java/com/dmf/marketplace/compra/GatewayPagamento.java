package com.dmf.marketplace.compra;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

public enum GatewayPagamento {
    PAGSEGURO {
        @Override
        public String criaUrlRetorno(Compra compra, UriComponentsBuilder uriComponentsBuilder) {
            UriComponents urlRetornoPagseguro = uriComponentsBuilder
                    .path("retorno-pagseguro/{idCompra}")
                    .buildAndExpand(compra.getId().toString());

            return String.format("pagseguro.com/%s?redirectUrl=%s", compra.getId(), urlRetornoPagseguro);
        }
    },

    PAYPAL {
        @Override
        public String criaUrlRetorno(Compra compra, UriComponentsBuilder uriComponentsBuilder) {
            UriComponents urlRetornoPayPal = uriComponentsBuilder
                    .path("retorno-paypal/{idCompra}")
                    .buildAndExpand(compra.getId().toString());

            return String.format("paypal.com/%s?redirectUrl=%s", compra.getId(), urlRetornoPayPal);
        }
    };

    public abstract String criaUrlRetorno(Compra compra, UriComponentsBuilder uriComponentsBuilder);

    public static GatewayPagamento fromString(String value) {
        return Arrays.stream(values())
                .filter(gateway -> gateway.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gateway de pagamento inválido: " + value));
    }

}

