package com.hcmunre.library.repository;

import com.hcmunre.library.entity.EmailOutbox;
import com.hcmunre.library.enums.TrangThaiEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, UUID> {
    List<EmailOutbox> findByTrangThai(TrangThaiEmail trangThaiEmail);
}
