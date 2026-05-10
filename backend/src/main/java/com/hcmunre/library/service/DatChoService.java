package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.DatChoRequest;
import com.hcmunre.library.dto.response.DatChoResponse;

import java.util.List;
import java.util.UUID;

public interface DatChoService {
    // Queries
    List<DatChoResponse> getAllDatCho();
    List<DatChoResponse> getDatChoByNguoiDung(UUID maNguoiDung);

    // Commands
    DatChoResponse createDatCho(DatChoRequest request);
    void cancelDatCho(UUID maDatCho, String ghiChuHuy);
}
