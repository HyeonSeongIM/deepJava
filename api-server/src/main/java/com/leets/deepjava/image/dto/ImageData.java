package com.leets.deepjava.image.dto;

import java.io.InputStream;

public record ImageData(InputStream stream, String contentType) {}
