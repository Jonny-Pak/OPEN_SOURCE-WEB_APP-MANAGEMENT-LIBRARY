package com.hcmunre.library.service;

import com.hcmunre.library.entity.PhieuPhat;
import java.util.List;
import java.util.UUID;

public interface PhieuPhatService {
    List<PhieuPhat> getAllPhieuPhat();
    PhieuPhat getPhieuPhatById(UUID id);
    PhieuPhat createPhieuPhat(PhieuPhat phieuPhat);
    PhieuPhat updatePhieuPhat(UUID id, PhieuPhat phieuPhat);
    void deletePhieuPhat(UUID id);
}
