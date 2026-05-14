package com.hcmunre.library.scheduler;

import com.hcmunre.library.entity.EmailOutbox;
import com.hcmunre.library.enums.TrangThaiEmail;
import com.hcmunre.library.repository.EmailOutboxRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailOutboxScheduler {
    private final EmailOutboxRepository emailOutboxRepository;
    private final JavaMailSender mailSender;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void xuLyEmailOutbox(){
        List<EmailOutbox> danhSachEmailDangCho = emailOutboxRepository.findByTrangThai(TrangThaiEmail.DANG_CHO);

        if(danhSachEmailDangCho.isEmpty()){
            return;
        }

        for(EmailOutbox emailOutbox : danhSachEmailDangCho){
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                helper.setTo(emailOutbox.getEmailNguoiNhan());
                helper.setSubject(emailOutbox.getTieuDe());
                helper.setText(emailOutbox.getNoiDung(), true);

                mailSender.send(mimeMessage);

                emailOutbox.setTrangThai(TrangThaiEmail.THANH_CONG);
            } catch (Exception e){
                log.error("Lỗi gửi email: {}", e.getMessage());
                emailOutbox.setSoLanThu(emailOutbox.getSoLanThu() + 1);
                emailOutbox.setLoi(e.getMessage());

                if(emailOutbox.getSoLanThu() >= 3){
                    emailOutbox.setTrangThai(TrangThaiEmail.THAT_BAI);
                }
            }
        }

        emailOutboxRepository.saveAll(danhSachEmailDangCho);
    }

}
