package com.stylish.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);
    private static final String UPLOAD_DIR = "stylish/src/main/resources/static/uploads/";

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath();
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.debug("Created upload directory: {}", uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = null;
        if (originalFileName != null) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = generateUniqueFileName() + fileExtension;

        Path filePath = uploadPath.resolve(uniqueFileName);
        file.transferTo(filePath.toFile());
        logger.debug("Saved image: {}", uniqueFileName);
        return "/uploads/" + uniqueFileName;
    }

    @Override
    public List<String> saveImages(List<MultipartFile> files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            imageUrls.add(saveImage(file));
        }
        logger.debug("Saved other images: {}", imageUrls);
        return imageUrls;
    }

    private String generateUniqueFileName() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}