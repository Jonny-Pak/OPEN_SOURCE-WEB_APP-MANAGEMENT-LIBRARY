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

    /** Chạy lúc 00:01 mỗi ngày — đánh dấu sách quá hạn */
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
                        "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>" +
                                "<h2 style='color:#dc2626'>⚠️ Sách quá hạn trả</h2>" +
                                "<p>Chào <b>" + nguoiDung.getHoTen() + "</b>,</p>" +
                                "<p>Cuốn sách <b>" + tenSach + "</b> của bạn đã quá hạn trả.</p>" +
                                "<p>Vui lòng mang đến thư viện để trả lại sách sớm nhất có thể để tránh phí phạt tăng thêm.</p>" +
                                "<p>Trân trọng,<br>Hệ thống Thư viện</p></div>"
                );
            }
        }

        log.info("CronJob: Đã cập nhật {} cuốn sách quá hạn.", dangMuonList.size());
    }

    /** Chạy lúc 08:00 mỗi ngày — nhắc nhở sách sắp đến hạn trả (ngày mai) */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void nhacNhoSachSapHanTra() {
        log.info("CronJob: Đang gửi nhắc nhở sách sắp đến hạn trả...");

        LocalDateTime tuGio = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime denGio = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59).withSecond(59);

        List<ChiTietPhieuMuon> sapHanList = chiTietPhieuMuonRepository
                .findByTrangThaiChiTietPhieuMuonAndHanTraHienTaiBetween(
                        TrangThaiChiTietPhieuMuon.DANG_MUON, tuGio, denGio);

        for (ChiTietPhieuMuon ct : sapHanList) {
            NguoiDung nguoiDung = ct.getPhieuMuon().getNguoiDung();
            String tenSach = ct.getCuonSach().getSach().getTenSach();

            thongBaoService.taoThongBao(nguoiDung.getMaNguoiDung(),
                    "Nhắc nhở: Sắp đến hạn trả sách",
                    "Cuốn sách '" + tenSach + "' của bạn sẽ đến hạn trả vào ngày mai. Vui lòng chuẩn bị trả sách đúng hạn.",
                    LoaiThongBao.NHAC_NHO
            );

            emailOutboxService.lenLichGuiEmail(nguoiDung.getEmail(),
                    "[Thư viện] Nhắc nhở: Sách sắp đến hạn trả",
                    "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>" +
                            "<h2 style='color:#d97706'>⏰ Sắp đến hạn trả sách</h2>" +
                            "<p>Chào <b>" + nguoiDung.getHoTen() + "</b>,</p>" +
                            "<p>Cuốn sách <b>" + tenSach + "</b> của bạn sẽ đến hạn trả vào <b>ngày mai</b> (" + ct.getHanTraHienTai().toLocalDate() + ").</p>" +
                            "<p>Vui lòng mang sách đến thư viện để trả đúng hạn và tránh bị phạt.</p>" +
                            "<p>Trân trọng,<br>Hệ thống Thư viện</p></div>"
            );
        }

        log.info("CronJob: Đã gửi {} nhắc nhở sách sắp hạn trả.", sapHanList.size());
    }
}
