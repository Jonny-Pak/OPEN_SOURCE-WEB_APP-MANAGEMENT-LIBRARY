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
     * Sử dụng ** để hỗ trợ tên file có dấu chấm và các ký tự đặc biệt.
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

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Xác định content-type theo đuôi file
            String contentType = detectContentType(filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    // Hiển thị inline (không bắt download)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    // Cache 7 ngày để tăng hiệu suất
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=604800")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String detectContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png"))  return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG_VALUE;
        if (lower.endsWith(".gif"))  return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg"))  return "image/svg+xml";
        if (lower.endsWith(".pdf"))  return MediaType.APPLICATION_PDF_VALUE;
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
