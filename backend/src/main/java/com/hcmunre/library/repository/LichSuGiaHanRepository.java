package com.hcmunre.library.repository;

import com.hcmunre.library.entity.LichSuGiaHan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LichSuGiaHanRepository extends JpaRepository<LichSuGiaHan, UUID> {
}
