package com.dmf.marketplace.compartilhado.aws;

import com.dmf.marketplace.compartilhado.ImageUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("prod")
public class S3ImageUploadService implements ImageUploadService {

    private static final Logger logger = LoggerFactory.getLogger(S3ImageUploadService.class);

    @Autowired
    private S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.endpoint}")
    private String endpoint;

    @Override
    public List<String> uploadImagesFromUrl(List<String> imageUrls) {
        Assert.notNull(imageUrls, "A lista de URLs não pode ser nula");
        return imageUrls.stream()
                .map(url -> {
                    try {
                        byte[] imageBytes = downloadImageFromUrl(url);
                        String fileName = generateUniqueFileName(getFileNameFromUrl(url));
                        String contentType = guessContentTypeFromUrl(url);
                        return uploadToS3(fileName, imageBytes, contentType);
                    } catch (IOException e) {
                        logger.error("Erro ao fazer upload da URL: {}", url, e);
                        throw new RuntimeException("Falha ao processar URL: " + url, e);
                    }
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<String> uploadImagesFromFiles(List<MultipartFile> files)  {
        Assert.notNull(files, "A lista de arquivos não pode ser nula");
        return files.stream()
                .map(file -> {
                    Assert.isTrue(!file.isEmpty(), "Arquivo inválido ou vazio: " + file.getOriginalFilename());
                    try {
                        String fileName = generateUniqueFileName(file.getOriginalFilename());
                        return uploadToS3(fileName, file.getBytes(), file.getContentType());
                    } catch (IOException e) {
                        logger.error("Erro ao fazer upload do arquivo: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("Falha ao processar arquivo: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    private String uploadToS3(String fileName, byte[] content, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
            return String.format("%s/%s/%s", endpoint, bucketName, fileName);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload para o S3: {}", fileName, e);
            throw new RuntimeException("Falha ao enviar arquivo para o S3: " + fileName, e);
        }
    }

    private String generateUniqueFileName(String originalName) {
        Assert.notNull(originalName, "Nome original do arquivo não pode ser nulo");
        return UUID.randomUUID() + "-" + originalName;
    }

    private String getFileNameFromUrl(String url) {
        Assert.notNull(url, "URL não pode ser nula");
        int lastSlashIndex = url.lastIndexOf("/");
        return lastSlashIndex == -1 ? url : url.substring(lastSlashIndex + 1);
    }

    private byte[] downloadImageFromUrl(String imageUrl) throws IOException {
        try {
            URL url = new URI(imageUrl).toURL();
            try (var inputStream = url.openStream()) {
                return inputStream.readAllBytes();
            }
        } catch (URISyntaxException e) {
            throw new IOException("URL inválida: " + imageUrl, e);
        }
    }

    private String guessContentTypeFromUrl(String url) {
        String fileName = getFileNameFromUrl(url);
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        return contentType != null ? contentType : "application/octet-stream";
    }
}
