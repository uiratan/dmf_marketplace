package com.dmf.marketplace.compartilhado.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Serviço para “upload” de imagens, suportando tanto URLs quanto arquivos diretamente enviados.
 */
public interface ImageUploadService {
    /**
     * Faz o “upload” de imagens a partir de uma lista de URLs.
     * @param imageUrls Lista de URLs das imagens a serem enviadas.
     * @return Lista de URLs das imagens armazenadas.
     */
    List<String> uploadImagesFromUrl(List<String> imageUrls);

    /**
     * Faz o “upload” de imagens a partir de uma lista de arquivos.
     * @param files Lista de arquivos MultipartFile a serem enviados.
     * @return Lista de URLs das imagens armazenadas.
     */
    List<String> uploadImagesFromFiles(List<MultipartFile> files);
}
