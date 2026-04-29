package com.hcmunre.library.repository;

import com.hcmunre.library.entity.PhieuMuon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhieuMuonRepository extends JpaRepository<PhieuMuon, UUID> {
}
