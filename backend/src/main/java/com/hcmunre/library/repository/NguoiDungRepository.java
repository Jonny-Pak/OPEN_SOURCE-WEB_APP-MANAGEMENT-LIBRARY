package com.hcmunre.library.repository;

import com.hcmunre.library_backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {
    Optional<NguoiDung> findByEmailOrSoDienThoai(String email, String soDienThoai);
}
