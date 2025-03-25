package com.dmf.marketplace.compartilhado;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile({"dev", "test"})
public class DevImageUploadService implements ImageUploadService {

    private static final String FAKE_BUCKET_URL = "http://fake-bucket.localhost/";

    /**
     * Gera URLs fictícias a partir de uma lista de URLs de imagens.
     */
    @Override
    public List<String> uploadImagesFromUrl(List<String> imageUrls) {
        Assert.notNull(imageUrls, "A lista de URLs não pode ser nula");
        return imageUrls.stream()
                .map(url -> generateFakeUrl(getFileNameFromUrl(url)))
                .collect(Collectors.toList());
    }

    /**
     * Gera URLs fictícias a partir de uma lista de arquivos.
     */
    @Override
    public List<String> uploadImagesFromFiles(List<MultipartFile> files) {
        Assert.notNull(files, "A lista de arquivos não pode ser nula");
        return files.stream()
                .map(file -> generateFakeUrl(file.getOriginalFilename()))
                .collect(Collectors.toList());
    }

    private String generateFakeUrl(String fileName) {
        return FAKE_BUCKET_URL + UUID.randomUUID() + "-" + fileName;
    }

    private String getFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        return lastSlashIndex == -1 ? url : url.substring(lastSlashIndex + 1);
    }
}