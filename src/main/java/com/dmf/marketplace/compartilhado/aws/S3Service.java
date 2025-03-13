package com.dmf.marketplace.compartilhado.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.endpoint}")
    private String endpoint;

    public List<String> uploadImages(List<String> imageUrls) throws IOException {
        List<String> s3Urls = new ArrayList<>();

        for (String imageUrl : imageUrls) {
            // Baixa o arquivo da URL
            byte[] imageBytes = downloadImageFromUrl(imageUrl);

            // Gera um nome único para o arquivo
            String fileName = System.currentTimeMillis() + "-" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // Cria a requisição para o S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType("image/jpeg") // Ajuste conforme o tipo de imagem
                    .build();

            // Faz o upload
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

            // URL ajustada para LocalStack (path-style)
            String s3Url = String.format("%s/%s/%s", endpoint, bucketName, fileName);
            s3Urls.add(s3Url);
        }

        return s3Urls;
    }

    private byte[] downloadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        try (var inputStream = url.openStream()) {
            return inputStream.readAllBytes();
        }
    }
}
