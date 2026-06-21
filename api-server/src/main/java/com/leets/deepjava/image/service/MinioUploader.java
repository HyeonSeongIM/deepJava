package com.leets.deepjava.image.service;

import com.leets.deepjava.image.config.MinioProperties;
import com.leets.deepjava.image.dto.ImageData;
import com.leets.deepjava.image.exception.ImageNotFoundException;
import com.leets.deepjava.image.exception.ImageUploadException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
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

    public ImageData download(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .build()
            );
            GetObjectResponse stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .build()
            );
            return new ImageData(stream, stat.contentType());
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new ImageNotFoundException("Image not found: " + objectName);
            }
            throw new ImageUploadException("Failed to get image: " + objectName, e);
        } catch (Exception e) {
            throw new ImageUploadException("Failed to get image: " + objectName, e);
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
