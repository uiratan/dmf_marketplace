package com.dmf.marketplace.compra;

import java.util.Arrays;
import java.util.UUID;

public enum GatewayPagamento {
    PAYPAL {
        @Override
        public String gerarUrlPagamento(UUID idCompra, String urlRetorno) {
            return String.format("https://paypal.com/%s?redirectUrl=%s", idCompra, urlRetorno);
        }
    },

    PAGSEGURO {
        @Override
        public String gerarUrlPagamento(UUID idCompra, String urlRetorno) {
            return String.format("https://pagseguro.com?returnId=%s&redirectUrl=%s", idCompra, urlRetorno);
        }
    };

    public abstract String gerarUrlPagamento(UUID idCompra, String urlRetorno);

    public static GatewayPagamento fromString(String value) {
        return Arrays.stream(values())
                .filter(gateway -> gateway.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gateway de pagamento inválido: " + value));
    }

}

