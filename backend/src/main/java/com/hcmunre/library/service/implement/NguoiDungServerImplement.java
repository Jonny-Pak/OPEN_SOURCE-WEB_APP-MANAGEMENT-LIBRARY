package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.service.NguoiDungService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NguoiDungServerImplement implements NguoiDungService {
    @Override
    public NguoiDung getNguoiDungActive(UUID maNugoiDung) {
        return null;
    }

    @Override
    public void checkNguoiDungPenalty(UUID maNguoiDung) {

    }
}
