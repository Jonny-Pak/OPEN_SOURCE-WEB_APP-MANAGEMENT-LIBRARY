package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.LienHeRequest;
import com.hcmunre.library.service.EmailOutboxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lien-he")
public class LienHeController {

    private final EmailOutboxService emailOutboxService;

    @PostMapping
    public ResponseEntity<Void> guiLienHe(@Valid @RequestBody LienHeRequest request) {
        String noiDungEmail = "<b>Từ:</b> " + request.getHoTen() + " (" + request.getEmail() + ")<br><br>" +
                "<b>Tiêu đề:</b> " + request.getTieuDe() + "<br><br>" +
                "<b>Nội dung:</b><br>" + request.getNoiDung();

        emailOutboxService.lenLichGuiEmail(
                request.getEmail(),
                "[Thư Viện] Liên hệ: " + request.getTieuDe(),
                noiDungEmail
        );

        return ResponseEntity.ok().build();
    }
}
