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

    private static final Path UPLOAD_ROOT = Paths.get("uploads/").toAbsolutePath().normalize();

    /**
     * Upload file chung (chỉ dành cho Quản trị viên / Thủ thư).
     * Dùng cho: ảnh sách, ảnh NXB, tài liệu, ...
     */
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

    /**
     * Upload avatar cho bất kỳ người dùng đã đăng nhập.
     * Cho phép DOC_GIA tự upload ảnh đại diện.
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            throw new LibraryException(ErrorCode.LOI_LUU_FILE);
        }
    }

    /**
     * Phục vụ file tĩnh (ảnh sách, avatar, ...).
     * Có kiểm tra path traversal để bảo mật.
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            // Bảo mật: chống path traversal attack
            Path filePath = UPLOAD_ROOT.resolve(filename).normalize();
            if (!filePath.startsWith(UPLOAD_ROOT)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                String lowerName = filename.toLowerCase();
                if (lowerName.endsWith(".png"))
                    contentType = MediaType.IMAGE_PNG_VALUE;
                else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg"))
                    contentType = MediaType.IMAGE_JPEG_VALUE;
                else if (lowerName.endsWith(".gif"))
                    contentType = MediaType.IMAGE_GIF_VALUE;
                else if (lowerName.endsWith(".webp"))
                    contentType = "image/webp";

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
