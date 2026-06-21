package com.leets.deepjava.image.service;

import com.leets.deepjava.image.config.MinioProperties;
import com.leets.deepjava.image.dto.ImageData;
import com.leets.deepjava.image.exception.ImageNotFoundException;
import com.leets.deepjava.image.exception.ImageUploadException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
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

    // ── upload ──────────────────────────────────────────────

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

    // ── download ─────────────────────────────────────────────

    @Test
    void download_returnsImageData() throws Exception {
        given(minioProperties.getBucket()).willReturn("images");
        StatObjectResponse stat = mock(StatObjectResponse.class);
        given(stat.contentType()).willReturn("image/jpeg");
        given(minioClient.statObject(any(StatObjectArgs.class))).willReturn(stat);
        given(minioClient.getObject(any(GetObjectArgs.class))).willReturn(mock(GetObjectResponse.class));

        ImageData result = minioUploader.download("test.jpg");

        assertThat(result.contentType()).isEqualTo("image/jpeg");
        assertThat(result.stream()).isNotNull();
    }

    @Test
    void download_objectNotFound_throwsImageNotFoundException() throws Exception {
        given(minioProperties.getBucket()).willReturn("images");
        ErrorResponse errorResponse = mock(ErrorResponse.class);
        given(errorResponse.code()).willReturn("NoSuchKey");
        ErrorResponseException notFoundEx = mock(ErrorResponseException.class);
        given(notFoundEx.errorResponse()).willReturn(errorResponse);
        doThrow(notFoundEx).when(minioClient).statObject(any(StatObjectArgs.class));

        assertThatThrownBy(() -> minioUploader.download("nonexistent.jpg"))
                .isInstanceOf(ImageNotFoundException.class);
    }

    @Test
    void download_minioThrows_wrapsAsImageUploadException() throws Exception {
        given(minioProperties.getBucket()).willReturn("images");
        given(minioClient.statObject(any(StatObjectArgs.class))).willThrow(new RuntimeException("connection failed"));

        assertThatThrownBy(() -> minioUploader.download("test.jpg"))
                .isInstanceOf(ImageUploadException.class);
    }
}
