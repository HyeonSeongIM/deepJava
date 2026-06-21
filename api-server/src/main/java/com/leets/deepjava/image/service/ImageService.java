package com.leets.deepjava.image.service;

import com.leets.deepjava.image.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioUploader minioUploader;

    public ImageUploadResponse upload(List<MultipartFile> files) {
        List<String> urls = files.stream()
                .map(minioUploader::upload)
                .toList();
        return new ImageUploadResponse(urls);
    }
}
