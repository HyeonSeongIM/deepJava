package com.leets.deepjava.image.controller;

import com.leets.deepjava.image.dto.ImageData;
import com.leets.deepjava.image.dto.ImageUploadResponse;
import com.leets.deepjava.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{objectName}")
    public ResponseEntity<Resource> download(@PathVariable String objectName) {
        ImageData data = imageService.getImage(objectName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(data.contentType()))
                .body(new InputStreamResource(data.stream()));
    }
}
