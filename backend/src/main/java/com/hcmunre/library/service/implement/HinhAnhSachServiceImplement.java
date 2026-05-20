package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.HinhAnhSachRequest;
import com.hcmunre.library.dto.request.HinhAnhSachUpdateRequest;
import com.hcmunre.library.dto.response.HinhAnhSachResponse;
import com.hcmunre.library.entity.HinhAnhSach;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.HinhAnhSachRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.service.HinhAnhSachService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HinhAnhSachServiceImplement implements HinhAnhSachService {

    private final HinhAnhSachRepository hinhAnhSachRepository;
    private final SachRepository sachRepository;

    private static final String UPLOAD_DIR = "uploads/";
    // Prefix returned by FileController — used to extract filename from stored path
    private static final String FILE_API_PREFIX = "/api/v1/files/";

    @Override
    public List<HinhAnhSachResponse> getAllHinhAnh() {
        return hinhAnhSachRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HinhAnhSachResponse> getHinhAnhBySach(Long maSach) {
        return hinhAnhSachRepository.findBySach_MaSach(maSach).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HinhAnhSachResponse getHinhAnhById(Long id) {
        HinhAnhSach hinhAnh = hinhAnhSachRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI));
        return mapToResponse(hinhAnh);
    }

    @Override
    public HinhAnhSachResponse createHinhAnh(HinhAnhSachRequest request) {
        Sach sach = sachRepository.findById(request.getMaSach())
                .orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));

        HinhAnhSach hinhAnhSach = HinhAnhSach.builder()
                .duongDan(request.getDuongDan())
                .loaiHinhAnh(request.getLoaiHinhAnh())
                .thuTuHienThi(request.getThuTuHienThi())
                .sach(sach)
                .build();

        HinhAnhSach saved = hinhAnhSachRepository.save(hinhAnhSach);
        return mapToResponse(saved);
    }

    @Override
    public HinhAnhSachResponse updateHinhAnh(Long id, HinhAnhSachUpdateRequest request) {
        HinhAnhSach hinhAnh = hinhAnhSachRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI));

        if (request.getLoaiHinhAnh() != null) {
            hinhAnh.setLoaiHinhAnh(request.getLoaiHinhAnh());
        }
        if (request.getThuTuHienThi() != null) {
            hinhAnh.setThuTuHienThi(request.getThuTuHienThi());
        }

        return mapToResponse(hinhAnhSachRepository.save(hinhAnh));
    }

    @Override
    public void deleteHinhAnh(Long id) {
        HinhAnhSach hinhAnh = hinhAnhSachRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI));

        // Xóa file vật lý trên ổ đĩa (nếu là file được lưu cục bộ)
        String duongDan = hinhAnh.getDuongDan();
        if (duongDan != null && duongDan.startsWith(FILE_API_PREFIX)) {
            String filename = duongDan.substring(FILE_API_PREFIX.length());
            // Bảo mật: chỉ xử lý tên file đơn giản (không có path traversal)
            if (!filename.contains("/") && !filename.contains("..") && !filename.isBlank()) {
                try {
                    Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
                    Files.deleteIfExists(filePath);
                    log.info("Đã xóa file vật lý: {}", filePath);
                } catch (IOException e) {
                    log.warn("Không thể xóa file vật lý '{}': {}", filename, e.getMessage());
                }
            }
        }

        hinhAnhSachRepository.deleteById(id);
    }

    private HinhAnhSachResponse mapToResponse(HinhAnhSach entity) {
        return HinhAnhSachResponse.builder()
                .maHinhAnh(entity.getMaHinhAnh())
                .duongDan(entity.getDuongDan())
                .loaiHinhAnh(entity.getLoaiHinhAnh())
                .thuTuHienThi(entity.getThuTuHienThi())
                .maSach(entity.getSach() != null ? entity.getSach().getMaSach() : null)
                .build();
    }
}
