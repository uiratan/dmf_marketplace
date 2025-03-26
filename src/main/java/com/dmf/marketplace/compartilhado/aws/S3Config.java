package com.dmf.marketplace.compartilhado.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.util.List;

@Configuration
@Profile("prod")
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.endpoint}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint)) // Define o endpoint do LocalStack
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Força o uso de path-style
                        .build())
                .build();
    }

    /**
     * Método para esvaziar um bucket S3 removendo todos os objetos contidos nele.
     * @param bucketName Nome do bucket a ser esvaziado.
     */
    public void emptyBucket(String bucketName) {
        S3Client s3 = s3Client();

        // Listar objetos no bucket
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);
        List<S3Object> objects = listResponse.contents();

        if (!objects.isEmpty()) {
            // Criar lista de objetos a serem excluídos
            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder()
                            .objects(objects.stream()
                                    .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                                    .toList())
                            .build())
                    .build();

            s3.deleteObjects(deleteRequest);
            System.out.println("Todos os objetos foram removidos do bucket: " + bucketName);
        } else {
            System.out.println("O bucket já está vazio.");
        }
    }

    /**
     * Método opcional para excluir o bucket após esvaziá-lo.
     * @param bucketName Nome do bucket a ser excluído.
     */
    public void deleteBucket(String bucketName) {
        S3Client s3 = s3Client();

        emptyBucket(bucketName); // Primeiro, esvaziar o bucket

        // Excluir o bucket
        s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        System.out.println("Bucket deletado: " + bucketName);
    }

}