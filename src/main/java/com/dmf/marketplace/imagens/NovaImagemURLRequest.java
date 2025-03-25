package com.dmf.marketplace.imagens;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NovaImagemURLRequest(
        @NotNull
        @Size(min = 1)
        List<String> imagens
) {
}
