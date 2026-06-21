package com.leets.deepjava.image.service;

import com.leets.deepjava.image.config.MinioProperties;
import com.leets.deepjava.image.domain.exception.ImageUploadException;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MinioUploaderTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @InjectMocks
    private MinioUploader minioUploader;

    @Test
    void upload_bucketExists_returnsUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        given(minioProperties.getBucket()).willReturn("images");
        given(minioProperties.getEndpoint()).willReturn("http://localhost:9000");
        given(minioClient.bucketExists(any(BucketExistsArgs.class))).willReturn(true);

        String url = minioUploader.upload(file);

        assertThat(url).startsWith("http://localhost:9000/images/");
        assertThat(url).endsWith(".jpg");
        then(minioClient).should().putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_bucketNotExists_createsBucketThenUploads() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());
        given(minioProperties.getBucket()).willReturn("images");
        given(minioProperties.getEndpoint()).willReturn("http://localhost:9000");
        given(minioClient.bucketExists(any(BucketExistsArgs.class))).willReturn(false);

        String url = minioUploader.upload(file);

        then(minioClient).should().makeBucket(any());
        assertThat(url).endsWith(".png");
    }

    @Test
    void upload_minioThrows_wrapsAsImageUploadException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        given(minioProperties.getBucket()).willReturn("images");
        given(minioClient.bucketExists(any(BucketExistsArgs.class))).willThrow(new RuntimeException("connection failed"));

        assertThatThrownBy(() -> minioUploader.upload(file))
                .isInstanceOf(ImageUploadException.class);
    }
}
