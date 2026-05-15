package com.hcmunre.library.service.implement;

import com.hcmunre.library.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;

@Service
public class FileServiceImplement implements FileService {

    private final String UPLOAD_DIR = "uploads/";

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new LibraryException(ErrorCode.FILE_TRONG);
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFilename);
        
        Files.copy(file.getInputStream(), filePath);

        // Return relative URL that points to our FileController
        return "/api/v1/files/" + newFilename;
    }
}
