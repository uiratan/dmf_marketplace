package com.dmf.marketplace.compra;

import java.util.Arrays;

public enum StatusCompra {
    INICIADA,
    PAGA,
    CANCELADA;

    public static StatusCompra fromString(String value) {
        return Arrays.stream(StatusCompra.values())
                .filter(s -> s.name().equalsIgnoreCase(value)) // Ignora maiúsculas e minúsculas
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status de compra inválido: " + value));
    }
}
