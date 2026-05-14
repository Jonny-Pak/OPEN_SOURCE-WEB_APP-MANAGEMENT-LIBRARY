package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.EmailOutbox;
import com.hcmunre.library.enums.TrangThaiEmail;
import com.hcmunre.library.repository.EmailOutboxRepository;
import com.hcmunre.library.service.EmailOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailOutboxServiceImplement implements EmailOutboxService {
    private final EmailOutboxRepository emailOutboxRepository;

    @Override
    public void lenLichGuiEmail(String emailNguoiNhan, String tieuDe, String noiDung){
        EmailOutbox outbox = EmailOutbox.builder()
                .emailNguoiNhan(emailNguoiNhan)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .trangThai(TrangThaiEmail.DANG_CHO)
                .soLanThu(0)
                .build();

        emailOutboxRepository.save(outbox);
    }


}
