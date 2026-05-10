package com.hcmunre.library.service;

import com.hcmunre.library.entity.LichSuGiaHan;
import java.util.List;
import java.util.UUID;

public interface LichSuGiaHanService {
    // Queries
    List<LichSuGiaHan> getAllLichSuGiaHan();
    LichSuGiaHan getLichSuGiaHanById(UUID id);

    // Commands
    LichSuGiaHan createLichSuGiaHan(LichSuGiaHan lichSuGiaHan);
    LichSuGiaHan updateLichSuGiaHan(UUID id, LichSuGiaHan lichSuGiaHan);
    void deleteLichSuGiaHan(UUID id);
}
