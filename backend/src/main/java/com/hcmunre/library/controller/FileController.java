package com.hcmunre.library.controller;

import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('QUAN_TRI_VIEN', 'THU_THU')")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            throw new LibraryException(ErrorCode.LOI_LUU_FILE);
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads/").resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = "application/octet-stream";
                if (filename.toLowerCase().endsWith(".png"))
                    contentType = MediaType.IMAGE_PNG_VALUE;
                else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg"))
                    contentType = MediaType.IMAGE_JPEG_VALUE;
                else if (filename.toLowerCase().endsWith(".gif"))
                    contentType = MediaType.IMAGE_GIF_VALUE;

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
