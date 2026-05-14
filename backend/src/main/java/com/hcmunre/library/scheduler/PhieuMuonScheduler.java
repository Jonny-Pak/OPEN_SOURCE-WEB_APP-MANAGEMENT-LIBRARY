package com.hcmunre.library.scheduler;

import com.hcmunre.library.entity.ChiTietPhieuMuon;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.LoaiThongBao;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
import com.hcmunre.library.service.EmailOutboxService;
import com.hcmunre.library.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhieuMuonScheduler {
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
    private final ThongBaoService thongBaoService;
    private final EmailOutboxService emailOutboxService;

    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void markOverdueChiTietPhieuMuon() {
        log.info("CronJob: Đang quét các chi tiết mượn sách quá hạn...");

        LocalDateTime now = LocalDateTime.now();
        List<ChiTietPhieuMuon> dangMuonList = chiTietPhieuMuonRepository
                .findByTrangThaiChiTietPhieuMuonAndHanTraHienTaiBefore(TrangThaiChiTietPhieuMuon.DANG_MUON, now);

        for (ChiTietPhieuMuon ct : dangMuonList) {
            if (now.isAfter(ct.getHanTraHienTai())) {
                ct.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.QUA_HAN);
                chiTietPhieuMuonRepository.save(ct);

                NguoiDung nguoiDung = ct.getPhieuMuon().getNguoiDung();
                String tenSach = ct.getCuonSach().getSach().getTenSach();

                thongBaoService.taoThongBao(ct.getPhieuMuon().getNguoiDung().getMaNguoiDung(),
                        "Sách quá hạn trả",
                        "Cuốn sách '" + tenSach + "' của bạn đã quá hạn trả. Vui lòng hoàn trả sớm để tránh phát sinh thêm phí phạt.",
                        LoaiThongBao.NHAC_NHO
                );

                emailOutboxService.lenLichGuiEmail(nguoiDung.getEmail(),
                        "[Thư viện] Thông báo sách quá hạn",
                        "Chào " + nguoiDung.getHoTen() + ",<br><br>" +
                                "Hệ thống ghi nhận cuốn sách <b>" + tenSach + "</b> của bạn đã quá hạn. " +
                                "Vui lòng mang đến thư viện để trả lại sách sớm nhất có thể.<br><br>" +
                                "Trân trọng!"
                        );
            }
        }

        log.info("CronJob: Đã cập nhật {} cuốn sách quá hạn.", dangMuonList.size());
    }
}
