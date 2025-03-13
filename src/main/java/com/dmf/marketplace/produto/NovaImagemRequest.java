package com.dmf.marketplace.produto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NovaImagemRequest(
        @Size(min = 1)
        List<String> imagens
) {
}
