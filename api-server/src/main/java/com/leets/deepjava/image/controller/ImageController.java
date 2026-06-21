package com.leets.deepjava.image.controller;

import com.leets.deepjava.image.dto.ImageUploadResponse;
import com.leets.deepjava.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(imageService.upload(files));
    }
}
