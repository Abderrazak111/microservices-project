package com.gestion.incidents.commentservice.service;

import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    private final MinioClient minioClient;

    @Value("${minio.bucket.commentaires:commentaires-pieces-jointes}")
    private String bucket;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFichier(MultipartFile fichier, UUID commentaireId) throws Exception {
        creerBucketSiNecessaire();
        String key = "commentaires/" + commentaireId + "/" + UUID.randomUUID() + "_" + fichier.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucket).object(key)
            .stream(fichier.getInputStream(), fichier.getSize(), -1)
            .contentType(fichier.getContentType()).build());
        log.info("Fichier uploadé: {}", key);
        return key;
    }

    public String genererUrlPresignee(String key) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(bucket).object(key).method(Method.GET).expiry(1, TimeUnit.HOURS).build());
    }

    public void supprimerFichier(String key) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
    }

    public String getNomBucket() { return bucket; }

    private void creerBucketSiNecessaire() throws Exception {
        boolean existe = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!existe) minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }
}
