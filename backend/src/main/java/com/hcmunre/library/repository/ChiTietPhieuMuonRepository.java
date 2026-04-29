package com.hcmunre.library.repository;

import com.hcmunre.library.entity.ChiTietPhieuMuon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChiTietPhieuMuonRepository extends JpaRepository<ChiTietPhieuMuon, UUID> {
}
