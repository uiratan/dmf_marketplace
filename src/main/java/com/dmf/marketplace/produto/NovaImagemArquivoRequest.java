package com.dmf.marketplace.produto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record NovaImagemArquivoRequest(
        @NotNull
        @Size(min = 1)
        List<MultipartFile> imagens
) {
}
