package com.leets.deepjava.image.controller;

import com.leets.deepjava.image.exception.ImageUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ImageExceptionHandler {

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<Map<String, String>> handleImageUploadException(ImageUploadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code", "IMAGE_UPLOAD_FAILED", "message", e.getMessage()));
    }
}
