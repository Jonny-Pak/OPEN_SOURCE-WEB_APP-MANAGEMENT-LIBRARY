package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.TheLoai;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.TheLoaiRepository;
import com.hcmunre.library.service.TheLoaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.hcmunre.library.dto.request.TheLoaiRequest;
import com.hcmunre.library.dto.response.TheLoaiResponse;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheLoaiServiceImplement implements TheLoaiService {

    private final TheLoaiRepository theLoaiRepository;

    @Override
    public List<TheLoaiResponse> getAllTheLoai() {
        return theLoaiRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TheLoaiResponse> searchTheLoai(String keyword) {
        return theLoaiRepository.findByTenTheLoaiContainingIgnoreCase(keyword).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public TheLoaiResponse getTheLoaiById(Long id) {
        return theLoaiRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.THE_LOAI_KHONG_TON_TAI));
    }

    @Override
    public TheLoaiResponse createTheLoai(TheLoaiRequest request) {
        TheLoai theLoai = TheLoai.builder()
                .tenTheLoai(request.getTenTheLoai())
                .moTa(request.getMoTa())
                .ngayXoa(null)
                .build();
        return toResponse(theLoaiRepository.save(theLoai));
    }

    @Override
    public TheLoaiResponse updateTheLoai(Long id, TheLoaiRequest request) {
        TheLoai theLoai = theLoaiRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.THE_LOAI_KHONG_TON_TAI));
        
        theLoai.setTenTheLoai(request.getTenTheLoai());
        theLoai.setMoTa(request.getMoTa());
        return toResponse(theLoaiRepository.save(theLoai));
    }

    @Override
    public void deleteTheLoai(Long id) {
        if (!theLoaiRepository.existsById(id)) {
            throw new LibraryException(ErrorCode.THE_LOAI_KHONG_TON_TAI);
        }
        theLoaiRepository.deleteById(id);
    }

    private TheLoaiResponse toResponse(TheLoai theLoai) {
        return TheLoaiResponse.builder()
                .maTheLoai(theLoai.getMaTheLoai())
                .tenTheLoai(theLoai.getTenTheLoai())
                .moTa(theLoai.getMoTa())
                .build();
    }
}
