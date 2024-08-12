package com.stylish.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {
    String saveImage(MultipartFile file) throws IOException;
    List<String> saveImages(List<MultipartFile> files) throws IOException;
}
