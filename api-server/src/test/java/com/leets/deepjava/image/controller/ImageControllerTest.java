package com.leets.deepjava.image.controller;

import com.leets.deepjava.image.dto.ImageData;
import com.leets.deepjava.image.dto.ImageUploadResponse;
import com.leets.deepjava.image.exception.ImageNotFoundException;
import com.leets.deepjava.image.exception.ImageUploadException;
import com.leets.deepjava.image.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    // ── upload ──────────────────────────────────────────────

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

    // ── download ─────────────────────────────────────────────

    @Test
    void download_returnsImageWithCorrectContentType() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("imagedata".getBytes());
        given(imageService.getImage("test.jpg")).willReturn(new ImageData(stream, "image/jpeg"));

        mockMvc.perform(get("/api/images/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    void download_imageNotFound_returns404() throws Exception {
        given(imageService.getImage(anyString()))
                .willThrow(new ImageNotFoundException("Image not found: nonexistent.jpg"));

        mockMvc.perform(get("/api/images/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }
}
