package com.hcmunre.library.scheduler;

import com.hcmunre.library.entity.DatCho;
import com.hcmunre.library.enums.LoaiThongBao;
import com.hcmunre.library.enums.TrangThaiDatCho;
import com.hcmunre.library.repository.DatChoRepository;
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
public class DatChoScheduler {
    private final DatChoRepository datChoRepository;
    private final ThongBaoService thongBaoService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cancelDatChoQuaHan(){
        log.info("CronJob: Đang quét các đặt chỗ quá hạn...");

        LocalDateTime now = LocalDateTime.now();
        List<DatCho> hetHanList = datChoRepository.findByTrangThaiAndHanGiuChoBefore(TrangThaiDatCho.DANG_CHO, now);

        for (DatCho datCho : hetHanList){
            datCho.setTrangThai(TrangThaiDatCho.HET_HAN);
            datCho.setGhiChuHuy("Hệ thống tự động huỷ do quá hạn không nhận sách");
            datChoRepository.save(datCho);

            thongBaoService.taoThongBao(datCho.getNguoiDung().getMaNguoiDung(),
                    "Huỷ đặt chỗ tự động",
                    "Lượt đặt chỗ sách '" + datCho.getSach().getTenSach() + "' của bạn đã bị huỷ do quá thời gian giữ sách.",
                    LoaiThongBao.HE_THONG
            );
        }

        log.info("CronJob: Đã hủy {} lượt đặt chỗ quá hạn.", hetHanList.size());
    }
}
