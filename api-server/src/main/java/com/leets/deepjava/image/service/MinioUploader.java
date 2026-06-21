package com.leets.deepjava.image.service;

import com.leets.deepjava.image.config.MinioProperties;
import com.leets.deepjava.image.exception.ImageUploadException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioUploader {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile file) {
        try {
            ensureBucketExists();
            String objectName = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/" + objectName;
        } catch (Exception e) {
            throw new ImageUploadException("Failed to upload: " + file.getOriginalFilename(), e);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build()
            );
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
