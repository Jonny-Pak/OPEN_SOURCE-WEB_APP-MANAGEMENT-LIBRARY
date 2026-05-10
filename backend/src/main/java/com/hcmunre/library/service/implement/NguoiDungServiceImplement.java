package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImplement implements NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public NguoiDung getNguoiDungActive(UUID maNugoiDung) {
        return nguoiDungRepository.findById(maNugoiDung).orElse(null);
    }

    @Override
    public void checkNguoiDungPenalty(UUID maNguoiDung) {

    }
}
