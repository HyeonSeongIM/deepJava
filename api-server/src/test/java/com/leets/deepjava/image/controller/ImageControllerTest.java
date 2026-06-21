package com.leets.deepjava.image.controller;

import com.leets.deepjava.image.exception.ImageUploadException;
import com.leets.deepjava.image.dto.ImageUploadResponse;
import com.leets.deepjava.image.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    void upload_returns200WithUrls() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "data".getBytes());
        given(imageService.upload(anyList()))
                .willReturn(new ImageUploadResponse(List.of("http://localhost:9000/images/test.jpg")));

        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urls[0]").value("http://localhost:9000/images/test.jpg"));
    }

    @Test
    void upload_returns500_whenUploadFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "data".getBytes());
        given(imageService.upload(anyList()))
                .willThrow(new ImageUploadException("upload failed", new RuntimeException()));

        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isInternalServerError());
    }
}
