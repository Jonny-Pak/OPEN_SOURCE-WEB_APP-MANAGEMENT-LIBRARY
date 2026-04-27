package com.hcmunre.library.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class RoleDemoController {

    @GetMapping("/api/reader/ping")
    @PreAuthorize("hasAnyRole('DOC_GIA','THU_THU','QUAN_TRI_VIEN')")
    public Map<String, String> readerPing(Authentication authentication) {
        return Map.of("message", "Reader access granted", "user", authentication.getName());
    }

    @GetMapping("/api/librarian/ping")
    @PreAuthorize("hasAnyRole('THU_THU','QUAN_TRI_VIEN')")
    public Map<String, String> librarianPing(Authentication authentication) {
        return Map.of("message", "Librarian access granted", "user", authentication.getName());
    }

    @GetMapping("/api/admin/ping")
    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    public Map<String, String> adminPing(Authentication authentication) {
        return Map.of("message", "Admin access granted", "user", authentication.getName());
    }
}
