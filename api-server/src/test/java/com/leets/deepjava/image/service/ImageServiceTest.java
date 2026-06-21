package com.leets.deepjava.image.service;

import com.leets.deepjava.image.dto.ImageData;
import com.leets.deepjava.image.dto.ImageUploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private MinioUploader minioUploader;

    @InjectMocks
    private ImageService imageService;

    @Test
    void upload_returnsUrlsForAllFiles() {
        MockMultipartFile file1 = new MockMultipartFile("files", "a.jpg", "image/jpeg", "d".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "b.jpg", "image/jpeg", "d".getBytes());
        given(minioUploader.upload(file1)).willReturn("http://localhost:9000/images/a-uuid.jpg");
        given(minioUploader.upload(file2)).willReturn("http://localhost:9000/images/b-uuid.jpg");

        ImageUploadResponse response = imageService.upload(List.of(file1, file2));

        assertThat(response.urls()).containsExactly(
                "http://localhost:9000/images/a-uuid.jpg",
                "http://localhost:9000/images/b-uuid.jpg"
        );
    }

    @Test
    void getImage_returnsImageData() {
        ImageData expected = new ImageData(new ByteArrayInputStream("data".getBytes()), "image/jpeg");
        given(minioUploader.download("test.jpg")).willReturn(expected);

        ImageData result = imageService.getImage("test.jpg");

        assertThat(result.contentType()).isEqualTo("image/jpeg");
        assertThat(result.stream()).isNotNull();
    }
}
