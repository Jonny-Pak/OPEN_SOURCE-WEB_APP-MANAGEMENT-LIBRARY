package com.hcmunre.library.scheduler;

import com.hcmunre.library.entity.ChiTietPhieuMuon;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
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
    private final com.hcmunre.library.repository.DatChoRepository datChoRepository;

    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void markOverdueChiTietPhieuMuon() {
        log.info("CronJob: Đang quét các chi tiết mượn sách quá hạn...");

        LocalDateTime now  = LocalDateTime.now();
        List<ChiTietPhieuMuon> dangMuonList = chiTietPhieuMuonRepository.findByTrangThaiChiTietPhieuMuonAndHanTraHienTaiBefore(TrangThaiChiTietPhieuMuon.DANG_MUON, now);

        for (ChiTietPhieuMuon ct : dangMuonList) {
            if (now.isAfter(ct.getHanTraHienTai())) {
                ct.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.QUA_HAN);
                chiTietPhieuMuonRepository.save(ct);
            }
        }

        log.info("CronJob: Đã cập nhật {} cuốn sách quá hạn.", dangMuonList.size());
    }

    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional
    public void markExpiredDatCho() {
        log.info("CronJob: Đang quét các phiếu đặt chỗ hết hạn...");

        LocalDateTime now = LocalDateTime.now();
        List<com.hcmunre.library.entity.DatCho> expiredList = datChoRepository.findByTrangThaiAndHanGiuChoBefore(
                com.hcmunre.library.enums.TrangThaiDatCho.DANG_CHO, now);

        for (com.hcmunre.library.entity.DatCho datCho : expiredList) {
            datCho.setTrangThai(com.hcmunre.library.enums.TrangThaiDatCho.HET_HAN);
            datCho.setGhiChuHuy("Hệ thống tự động huỷ do quá thời hạn giữ chỗ");
            datChoRepository.save(datCho);
        }

        log.info("CronJob: Đã tự động huỷ {} phiếu đặt chỗ hết hạn.", expiredList.size());
    }
}
