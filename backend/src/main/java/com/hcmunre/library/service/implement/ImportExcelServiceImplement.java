package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.response.ImportExcelResponse;
import com.hcmunre.library.entity.*;
import com.hcmunre.library.enums.*;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.*;
import com.hcmunre.library.service.ImportExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExcelServiceImplement implements ImportExcelService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final SachRepository sachRepository;
    private final CuonSachRepository cuonSachRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final TacGiaRepository tacGiaRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final HinhAnhSachRepository hinhAnhSachRepository;

    private static final String DEFAULT_PASSWORD = "Password@123";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Path UPLOAD_ROOT = java.nio.file.Paths.get("uploads/").toAbsolutePath().normalize();

    // ==========================
    // IMPORT NGƯỜI DÙNG / ĐỘC GIẢ
    // Cột: STT(0) | Mã SV(1) | Họ tên(2) | Giới tính(3) | Ngày sinh(4) | Số điện
    // thoại(5) | CCCD(6) | Email(7)
    // ==========================
    @Override
    @Transactional
    public ImportExcelResponse importNguoiDung(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }

        List<String> danhSachLoi = new ArrayList<>();
        int thanhCong = 0;
        int tongSoDong = 0;

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Kiểm tra header (dòng 0)
            Row headerRow = sheet.getRow(0);
            validateNguoiDungHeader(headerRow);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row, 8))
                    continue;

                tongSoDong++;
                try {
                    nguoiDungRepository.save(parseNguoiDungRow(row, i + 1));
                    thanhCong++;
                } catch (LibraryException e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": " + e.getErrorCode().getMessage());
                } catch (Exception e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": Lỗi không xác định — " + e.getMessage());
                }
            }
        } catch (LibraryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi đọc file Excel người dùng: {}", e.getMessage(), e);
            throw new LibraryException(ErrorCode.LOI_DOC_FILE_EXCEL);
        }

        log.info("Import Excel người dùng hoàn tất: {}/{} thành công, {} lỗi",
                thanhCong, tongSoDong, danhSachLoi.size());

        return ImportExcelResponse.builder()
                .tongSoDong(tongSoDong)
                .thanhCong(thanhCong)
                .thatBai(danhSachLoi.size())
                .danhSachLoi(danhSachLoi)
                .build();
    }

    private void validateNguoiDungHeader(Row headerRow) {
        if (headerRow == null) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }
        // Kiểm tra tối thiểu 8 cột
        String[] expectedHeaders = { "STT", "Mã SV", "Họ tên", "Giới tính", "Ngày sinh", "Số điện thoại", "CCCD",
                "Email" };
        for (int i = 0; i < expectedHeaders.length; i++) {
            Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
            }
        }
    }

    private NguoiDung parseNguoiDungRow(Row row, int rowNum) {
        // Cột: STT(0) | Mã SV(1) | Họ tên(2) | Giới tính(3) | Ngày sinh(4) | SĐT(5) |
        // CCCD(6) | Email(7)
        String mssv = cellStr(row, 1);
        String hoTenStr = cellStr(row, 2);
        String gtStr = cellStr(row, 3);
        String nsStr = cellStr(row, 4);
        String sdt = cellStr(row, 5);
        String cccd = cellStr(row, 6);
        String email = cellStr(row, 7);

        // Validate bắt buộc
        if (mssv == null || mssv.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_EMAIL); // thiếu mã SV
        }
        if (hoTenStr == null || hoTenStr.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_TEN);
        }
        if (email == null || email.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_EMAIL);
        }
        if (sdt == null || sdt.isBlank()) {
            throw new LibraryException(ErrorCode.SDT_KHONG_HOP_LE);
        }

        // Validate email format
        if (!email.trim().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new LibraryException(ErrorCode.EMAIL_KHONG_HOP_LE);
        }
        // Validate SĐT
        if (!sdt.trim().matches("^0\\d{9}$")) {
            throw new LibraryException(ErrorCode.SDT_KHONG_HOP_LE);
        }

        // Kiểm tra trùng
        if (nguoiDungRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        if (nguoiDungRepository.existsBySoDienThoai(sdt.trim())) {
            throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
        }
        if (cccd != null && !cccd.isBlank() && nguoiDungRepository.existsByCccd(cccd.trim())) {
            throw new LibraryException(ErrorCode.CCCD_DA_TON_TAI);
        }

        // Tách họ tên
        hoTenStr = hoTenStr.trim();
        int lastSpaceIdx = hoTenStr.lastIndexOf(' ');
        String hoDem;
        String ten;
        if (lastSpaceIdx > 0) {
            hoDem = hoTenStr.substring(0, lastSpaceIdx).trim();
            ten = hoTenStr.substring(lastSpaceIdx + 1).trim();
        } else {
            hoDem = "Sinh viên";
            ten = hoTenStr;
        }

        // Ngày sinh
        LocalDate ngaySinh = null;
        if (nsStr != null && !nsStr.isBlank()) {
            try {
                ngaySinh = LocalDate.parse(nsStr.trim(), DATE_FMT);
            } catch (DateTimeParseException e) {
                throw new LibraryException(ErrorCode.EXCEL_NGAY_SINH_SAI_FORMAT);
            }
        }

        // Giới tính
        GioiTinh gioiTinh = null;
        if (gtStr != null && !gtStr.isBlank()) {
            String gtClean = gtStr.toUpperCase().trim();
            if (gtClean.equals("NAM")) {
                gioiTinh = GioiTinh.NAM;
            } else if (gtClean.equals("NỮ") || gtClean.equals("NU")) {
                gioiTinh = GioiTinh.NU;
            } else {
                throw new LibraryException(ErrorCode.EXCEL_GIOI_TINH_KHONG_HOP_LE);
            }
        }

        // CCCD validation (nếu có)
        String cccdVal = null;
        if (cccd != null && !cccd.isBlank()) {
            if (!cccd.trim().matches("^\\d{12}$")) {
                throw new LibraryException(ErrorCode.CCCD_KHONG_HOP_LE);
            }
            cccdVal = cccd.trim();
        }

        return NguoiDung.builder()
                .hoDem(hoDem)
                .ten(ten)
                .email(email.trim().toLowerCase())
                .soDienThoai(sdt.trim())
                .matKhau(passwordEncoder.encode(DEFAULT_PASSWORD))
                .ngaySinh(ngaySinh)
                .gioiTinh(gioiTinh)
                .cccd(cccdVal)
                .vaiTro(VaiTro.DOC_GIA)
                .trangThai(TrangThaiNguoiDung.CHUA_KICH_HOAT)
                .build();
    }

    // ==========================
    // IMPORT CUỐN SÁCH (BẢN SAO)
    // Cột: STT(0) | Mã ISBN/Mã sách(1) | Mã vạch(2) | Vị trí kệ(3) | Tình trạng vật
    // lý(4) | Ghi chú(5)
    // ==========================
    @Override
    @Transactional
    public ImportExcelResponse importCuonSach(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }

        List<String> danhSachLoi = new ArrayList<>();
        int thanhCong = 0;
        int tongSoDong = 0;

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Kiểm tra header
            Row headerRow = sheet.getRow(0);
            validateCuonSachHeader(headerRow);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row, 6))
                    continue;

                tongSoDong++;
                try {
                    cuonSachRepository.save(parseCuonSachRow(row, i + 1));
                    thanhCong++;
                } catch (LibraryException e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": " + e.getErrorCode().getMessage());
                } catch (Exception e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": Lỗi không xác định — " + e.getMessage());
                }
            }
        } catch (LibraryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi đọc file Excel cuốn sách: {}", e.getMessage(), e);
            throw new LibraryException(ErrorCode.LOI_DOC_FILE_EXCEL);
        }

        log.info("Import Excel cuốn sách hoàn tất: {}/{} thành công, {} lỗi",
                thanhCong, tongSoDong, danhSachLoi.size());

        return ImportExcelResponse.builder()
                .tongSoDong(tongSoDong)
                .thanhCong(thanhCong)
                .thatBai(danhSachLoi.size())
                .danhSachLoi(danhSachLoi)
                .build();
    }

    private void validateCuonSachHeader(Row headerRow) {
        if (headerRow == null) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }
        // Tối thiểu 3 cột: STT, Mã sách, Mã vạch
        for (int i = 0; i < 3; i++) {
            Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
            }
        }
    }

    private CuonSach parseCuonSachRow(Row row, int rowNum) {
        // Cột: STT(0) | Mã sách(1) | Mã vạch(2) | Vị trí kệ(3) | Tình trạng vật lý(4) |
        // Ghi chú(5)
        String maSachStr = cellStr(row, 1);
        String maVach = cellStr(row, 2);
        String viTriKe = cellStr(row, 3);
        String tinhTrangStr = cellStr(row, 4);
        String ghiChu = cellStr(row, 5);

        if (maSachStr == null || maSachStr.isBlank()) {
            throw new LibraryException(ErrorCode.SACH_KHONG_TON_TAI);
        }
        if (maVach == null || maVach.isBlank()) {
            throw new LibraryException(ErrorCode.MA_VACH_TRONG);
        }

        // Tìm đầu sách theo ISBN hoặc maSach
        Sach sach = null;
        try {
            Long maSachId = Long.parseLong(maSachStr.trim());
            sach = sachRepository.findById(maSachId).orElse(null);
        } catch (NumberFormatException e) {
            // thử tìm theo ISBN
            sach = sachRepository.findByMaIsbn(maSachStr.trim()).orElse(null);
        }
        if (sach == null) {
            throw new LibraryException(ErrorCode.SACH_KHONG_TON_TAI);
        }

        // Kiểm tra mã vạch đã tồn tại chưa
        if (cuonSachRepository.existsByMaVach(maVach.trim())) {
            throw new LibraryException(ErrorCode.MA_VACH_DA_TON_TAI);
        }

        // Tình trạng vật lý
        TinhTrangVatLy tinhTrang = TinhTrangVatLy.BINH_THUONG; // default
        if (tinhTrangStr != null && !tinhTrangStr.isBlank()) {
            try {
                tinhTrang = TinhTrangVatLy.valueOf(tinhTrangStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // map từ tiếng Việt
                String clean = tinhTrangStr.trim().toLowerCase();
                if (clean.contains("hư") || clean.contains("hu hong") || clean.contains("hỏng")) {
                    tinhTrang = TinhTrangVatLy.HONG_NANG;
                }
                // else giữ TOT
            }
        }

        return CuonSach.builder()
                .sach(sach)
                .maVach(maVach.trim())
                .viTriKe(viTriKe != null ? viTriKe.trim() : null)
                .tinhTrangVatLy(tinhTrang)
                .trangThai(TrangThaiCuonSach.SAN_SANG)
                .ghiChuBaoTri(ghiChu)
                .build();
    }

    // ==========================
    // IMPORT ĐẦU SÁCH
    // Cột: STT(0) | ISBN(1) | Tên sách(2) | Tác giả(3) | NXB(4) | Thể loại(5) | Năm XB(6)
    //       | Số trang(7) | Lần tái bản(8) | Giá tiền(9) | Phạt/ngày(10) | Mô tả(11)
    //       | Ảnh bìa trước(12) | Ảnh bìa sau(13) | Ảnh khác(14)
    // ==========================
    @Override
    @Transactional
    public ImportExcelResponse importSach(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }

        List<String> danhSachLoi = new ArrayList<>();
        int thanhCong = 0;
        int tongSoDong = 0;

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row, 3)) continue;

                tongSoDong++;
                try {
                    parseSachRow(row, i + 1);
                    thanhCong++;
                } catch (LibraryException e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": " + e.getErrorCode().getMessage());
                } catch (Exception e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": Lỗi — " + e.getMessage());
                }
            }
        } catch (LibraryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi đọc file Excel đầu sách: {}", e.getMessage(), e);
            throw new LibraryException(ErrorCode.LOI_DOC_FILE_EXCEL);
        }

        log.info("Import Excel đầu sách hoàn tất: {}/{} thành công, {} lỗi",
                thanhCong, tongSoDong, danhSachLoi.size());

        return ImportExcelResponse.builder()
                .tongSoDong(tongSoDong)
                .thanhCong(thanhCong)
                .thatBai(danhSachLoi.size())
                .danhSachLoi(danhSachLoi)
                .build();
    }

    /**
     * Parse một dòng Excel thành Sach + danh sách HinhAnhSach.
     * Tự động tìm hoặc tạo mới TacGia, NhaXuatBan, TheLoai theo tên.
     */
    private void parseSachRow(Row row, int rowNum) {
        String maIsbn   = cellStr(row, 1);
        String tenSach  = cellStr(row, 2);
        String tacGiaStr = cellStr(row, 3);
        String nxbStr   = cellStr(row, 4);
        String theLoaiStr = cellStr(row, 5);
        String namXbStr = cellStr(row, 6);
        String soTrangStr = cellStr(row, 7);
        String lanTaiStr = cellStr(row, 8);
        String giaStr   = cellStr(row, 9);
        String phatStr  = cellStr(row, 10);
        String moTa     = cellStr(row, 11);
        String anhBiaTruoc = cellStr(row, 12);
        String anhBiaSau   = cellStr(row, 13);
        String anhKhac     = cellStr(row, 14);

        // Validate bắt buộc
        if (tenSach == null || tenSach.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_TEN);
        }
        if (maIsbn == null || maIsbn.isBlank()) {
            // Tự sinh ISBN-13 hợp lệ về format (bắt đầu bằng 978)
            maIsbn = generateFallbackIsbn(rowNum);
        } else {
            String cleaned = maIsbn.replaceAll("[^0-9xX]", "").toUpperCase();
            maIsbn = cleaned.isBlank() ? generateFallbackIsbn(rowNum) : cleaned;
        }

        // Kiểm tra ISBN đã tồn tại
        if (sachRepository.findByMaIsbn(maIsbn).isPresent()) {
            throw new LibraryException(ErrorCode.ISBN_DA_TON_TAI);
        }

        // Năm xuất bản
        int namXuatBan = new java.util.Date().getYear() + 1900;
        if (namXbStr != null && !namXbStr.isBlank()) {
            try { namXuatBan = Integer.parseInt(namXbStr.trim()); } catch (NumberFormatException ignored) {}
        }

        int soTrang = 100;
        if (soTrangStr != null && !soTrangStr.isBlank()) {
            try { soTrang = Integer.parseInt(soTrangStr.trim()); } catch (NumberFormatException ignored) {}
        }

        int lanTaiBan = 1;
        if (lanTaiStr != null && !lanTaiStr.isBlank()) {
            try { lanTaiBan = Integer.parseInt(lanTaiStr.trim()); } catch (NumberFormatException ignored) {}
        }

        double giaTien = 100000;
        if (giaStr != null && !giaStr.isBlank()) {
            try { giaTien = Double.parseDouble(giaStr.trim()); } catch (NumberFormatException ignored) {}
        }

        double donGiaPhat = 5000;
        if (phatStr != null && !phatStr.isBlank()) {
            try { donGiaPhat = Double.parseDouble(phatStr.trim()); } catch (NumberFormatException ignored) {}
        }

        // Tìm NXB theo tên (nếu không tìm thấy để null — NXB không bắt buộc khi import)
        NhaXuatBan nhaXuatBan = null;
        if (nxbStr != null && !nxbStr.isBlank()) {
            List<NhaXuatBan> nxbList = nhaXuatBanRepository.findByTenNhaXuatBanContainingIgnoreCase(nxbStr.trim());
            nhaXuatBan = nxbList.isEmpty() ? null : nxbList.get(0);
        }

        // Tìm hoặc tạo TacGia
        List<TacGia> danhSachTacGia = new java.util.ArrayList<>();
        if (tacGiaStr != null && !tacGiaStr.isBlank()) {
            // Hỗ trợ nhiều tác giả cách nhau bằng dấu phẩy
            String[] tacGiaArr = tacGiaStr.split(",");
            for (String tgName : tacGiaArr) {
                String tgClean = tgName.trim();
                if (tgClean.isBlank()) continue;
                List<TacGia> found = tacGiaRepository.findByTenContainingIgnoreCase(tgClean);
                if (!found.isEmpty()) {
                    danhSachTacGia.add(found.get(0));
                } else {
                    // Tự tạo tác giả mới
                    int lastSpace = tgClean.lastIndexOf(' ');
                    String hoDem = lastSpace > 0 ? tgClean.substring(0, lastSpace).trim() : "Tác giả";
                    String ten   = lastSpace > 0 ? tgClean.substring(lastSpace + 1).trim() : tgClean;
                    TacGia newTg = TacGia.builder().hoDem(hoDem).ten(ten).build();
                    danhSachTacGia.add(tacGiaRepository.save(newTg));
                }
            }
        }

        // Tìm hoặc tạo TheLoai
        List<TheLoai> danhSachTheLoai = new java.util.ArrayList<>();
        if (theLoaiStr != null && !theLoaiStr.isBlank()) {
            String[] tlArr = theLoaiStr.split(",");
            for (String tlName : tlArr) {
                String tlClean = tlName.trim();
                if (tlClean.isBlank()) continue;
                List<TheLoai> found = theLoaiRepository.findByTenTheLoaiContainingIgnoreCase(tlClean);
                if (!found.isEmpty()) {
                    danhSachTheLoai.add(found.get(0));
                } else {
                    TheLoai newTl = TheLoai.builder().tenTheLoai(tlClean).build();
                    danhSachTheLoai.add(theLoaiRepository.save(newTl));
                }
            }
        }

        // Tạo đầu sách
        Sach sach = Sach.builder()
                .tenSach(tenSach.trim())
                .maIsbn(maIsbn)
                .namXuatBan(namXuatBan)
                .soTrang(soTrang)
                .lanTaiBan(lanTaiBan)
                .giaTien(giaTien)
                .donGiaPhatTheoNgay(donGiaPhat)
                .moTa(moTa)
                .nhaXuatBan(nhaXuatBan)
                .danhSachTacGia(danhSachTacGia)
                .danhSachTheLoai(danhSachTheLoai)
                .build();

        Sach savedSach = sachRepository.save(sach);

        // Gắn ảnh: Bìa trước → BIA_TRUOC (thứ tự 0)
        int thuTu = 0;
        if (anhBiaTruoc != null && !anhBiaTruoc.isBlank()) {
            HinhAnhSach ha = HinhAnhSach.builder()
                    .duongDan(anhBiaTruoc.trim())
                    .loaiHinhAnh(LoaiHinhAnh.BIA_TRUOC)
                    .thuTuHienThi(thuTu++)
                    .sach(savedSach)
                    .build();
            hinhAnhSachRepository.save(ha);
        }
        if (anhBiaSau != null && !anhBiaSau.isBlank()) {
            HinhAnhSach ha = HinhAnhSach.builder()
                    .duongDan(anhBiaSau.trim())
                    .loaiHinhAnh(LoaiHinhAnh.BIA_SAU)
                    .thuTuHienThi(thuTu++)
                    .sach(savedSach)
                    .build();
            hinhAnhSachRepository.save(ha);
        }
        if (anhKhac != null && !anhKhac.isBlank()) {
            // Có thể có nhiều ảnh khác cách nhau bằng dấu |
            String[] anhKhacArr = anhKhac.split("\\|");
            for (String url : anhKhacArr) {
                String urlClean = url.trim();
                if (!urlClean.isBlank()) {
                    HinhAnhSach ha = HinhAnhSach.builder()
                            .duongDan(urlClean)
                            .loaiHinhAnh(LoaiHinhAnh.KHAC)
                            .thuTuHienThi(thuTu++)
                            .sach(savedSach)
                            .build();
                    hinhAnhSachRepository.save(ha);
                }
            }
        }
    }

    // ==========================
    // IMPORT ĐẦU SÁCH TỪ FILE ZIP (Excel + Images)
    // ==========================

    @Override
    @Transactional
    public ImportExcelResponse importSachTuZip(MultipartFile zipFile) {
        String filename = zipFile.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }

        List<String> danhSachLoi = new ArrayList<>();
        int thanhCong = 0;
        int tongSoDong = 0;

        // Map: tên file ảnh (lowercase) → bytes
        Map<String, byte[]> imageMap = new HashMap<>();
        Workbook workbook = null;

        // Bước 1: Giải nén ZIP, tách Excel và ảnh
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) { zis.closeEntry(); continue; }

                String entryName = entry.getName();
                String lowerName = entryName.toLowerCase();
                byte[] bytes = zis.readAllBytes();
                zis.closeEntry();

                if (lowerName.endsWith(".xlsx")) {
                    if (workbook == null) {
                        workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes));
                    }
                } else if (isImageFile(lowerName)) {
                    // Lưu theo đường dẫn đầy đủ và tên file thuần
                    imageMap.put(lowerName, bytes);
                    String baseName = Paths.get(entryName).getFileName().toString().toLowerCase();
                    imageMap.put(baseName, bytes);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi giải nén ZIP: {}", e.getMessage(), e);
            throw new LibraryException(ErrorCode.LOI_DOC_FILE_EXCEL);
        }

        if (workbook == null) {
            throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
        }

        // Bước 2: Đọc từng dòng Excel
        try {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getRow(0) == null) {
                throw new LibraryException(ErrorCode.FILE_EXCEL_KHONG_HOP_LE);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row, 3)) continue;

                tongSoDong++;
                try {
                    parseSachRowVoiAnh(row, i + 1, imageMap);
                    thanhCong++;
                } catch (LibraryException e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": " + e.getErrorCode().getMessage());
                } catch (Exception e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": Lỗi — " + e.getMessage());
                }
            }
            workbook.close();
        } catch (LibraryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi xử lý Excel trong ZIP: {}", e.getMessage(), e);
            throw new LibraryException(ErrorCode.LOI_DOC_FILE_EXCEL);
        }

        log.info("Import ZIP đầu sách hoàn tất: {}/{} thành công, {} lỗi",
                thanhCong, tongSoDong, danhSachLoi.size());

        return ImportExcelResponse.builder()
                .tongSoDong(tongSoDong)
                .thanhCong(thanhCong)
                .thatBai(danhSachLoi.size())
                .danhSachLoi(danhSachLoi)
                .build();
    }

    /**
     * Parse dòng Excel kèm ảnh từ imageMap.
     * Tương tự parseSachRow nhưng resolve ảnh thật từ ZIP thay vì string URL thô.
     */
    private void parseSachRowVoiAnh(Row row, int rowNum, Map<String, byte[]> imageMap) throws IOException {
        String maIsbn     = cellStr(row, 1);
        String tenSach    = cellStr(row, 2);
        String tacGiaStr  = cellStr(row, 3);
        String nxbStr     = cellStr(row, 4);
        String theLoaiStr = cellStr(row, 5);
        String namXbStr   = cellStr(row, 6);
        String soTrangStr = cellStr(row, 7);
        String lanTaiStr  = cellStr(row, 8);
        String giaStr     = cellStr(row, 9);
        String phatStr    = cellStr(row, 10);
        String moTa       = cellStr(row, 11);
        String anhBiaTruoc = cellStr(row, 12);
        String anhBiaSau   = cellStr(row, 13);
        String anhKhac     = cellStr(row, 14);

        if (tenSach == null || tenSach.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_TEN);
        }
        if (maIsbn == null || maIsbn.isBlank()) {
            maIsbn = generateFallbackIsbn(rowNum);
        } else {
            String cleaned = maIsbn.replaceAll("[^0-9xX]", "").toUpperCase();
            maIsbn = cleaned.isBlank() ? generateFallbackIsbn(rowNum) : cleaned;
        }
        if (sachRepository.findByMaIsbn(maIsbn).isPresent()) {
            throw new LibraryException(ErrorCode.ISBN_DA_TON_TAI);
        }

        int namXuatBan = new java.util.Date().getYear() + 1900;
        if (namXbStr != null && !namXbStr.isBlank()) {
            try { namXuatBan = Integer.parseInt(namXbStr.trim()); } catch (NumberFormatException ignored) {}
        }
        int soTrang = 100;
        if (soTrangStr != null && !soTrangStr.isBlank()) {
            try { soTrang = Integer.parseInt(soTrangStr.trim()); } catch (NumberFormatException ignored) {}
        }
        int lanTaiBan = 1;
        if (lanTaiStr != null && !lanTaiStr.isBlank()) {
            try { lanTaiBan = Integer.parseInt(lanTaiStr.trim()); } catch (NumberFormatException ignored) {}
        }
        double giaTien = 100000;
        if (giaStr != null && !giaStr.isBlank()) {
            try { giaTien = Double.parseDouble(giaStr.trim()); } catch (NumberFormatException ignored) {}
        }
        double donGiaPhat = 5000;
        if (phatStr != null && !phatStr.isBlank()) {
            try { donGiaPhat = Double.parseDouble(phatStr.trim()); } catch (NumberFormatException ignored) {}
        }

        // Tìm NXB theo tên (nếu không tìm thấy để null — NXB không bắt buộc khi import)
        NhaXuatBan nhaXuatBan = null;
        if (nxbStr != null && !nxbStr.isBlank()) {
            List<NhaXuatBan> nxbList = nhaXuatBanRepository.findByTenNhaXuatBanContainingIgnoreCase(nxbStr.trim());
            nhaXuatBan = nxbList.isEmpty() ? null : nxbList.get(0);
        }

        // Tác giả
        List<TacGia> danhSachTacGia = new java.util.ArrayList<>();
        if (tacGiaStr != null && !tacGiaStr.isBlank()) {
            for (String tgName : tacGiaStr.split(",")) {
                String tgClean = tgName.trim();
                if (tgClean.isBlank()) continue;
                List<TacGia> found = tacGiaRepository.findByTenContainingIgnoreCase(tgClean);
                if (!found.isEmpty()) {
                    danhSachTacGia.add(found.get(0));
                } else {
                    int ls = tgClean.lastIndexOf(' ');
                    String hoDem = ls > 0 ? tgClean.substring(0, ls).trim() : "Tác giả";
                    String ten   = ls > 0 ? tgClean.substring(ls + 1).trim() : tgClean;
                    danhSachTacGia.add(tacGiaRepository.save(TacGia.builder().hoDem(hoDem).ten(ten).build()));
                }
            }
        }

        // Thể loại
        List<TheLoai> danhSachTheLoai = new java.util.ArrayList<>();
        if (theLoaiStr != null && !theLoaiStr.isBlank()) {
            for (String tlName : theLoaiStr.split(",")) {
                String tlClean = tlName.trim();
                if (tlClean.isBlank()) continue;
                List<TheLoai> found = theLoaiRepository.findByTenTheLoaiContainingIgnoreCase(tlClean);
                if (!found.isEmpty()) {
                    danhSachTheLoai.add(found.get(0));
                } else {
                    danhSachTheLoai.add(theLoaiRepository.save(TheLoai.builder().tenTheLoai(tlClean).build()));
                }
            }
        }

        Sach sach = Sach.builder()
                .tenSach(tenSach.trim())
                .maIsbn(maIsbn)
                .namXuatBan(namXuatBan)
                .soTrang(soTrang)
                .lanTaiBan(lanTaiBan)
                .giaTien(giaTien)
                .donGiaPhatTheoNgay(donGiaPhat)
                .moTa(moTa)
                .nhaXuatBan(nhaXuatBan)
                .danhSachTacGia(danhSachTacGia)
                .danhSachTheLoai(danhSachTheLoai)
                .build();
        Sach savedSach = sachRepository.save(sach);

        // Gắn ảnh — resolve từ imageMap (ảnh ZIP) hoặc URL trực tiếp
        int thuTu = 0;
        if (anhBiaTruoc != null && !anhBiaTruoc.isBlank()) {
            String url = resolveImageUrl(anhBiaTruoc.trim(), imageMap);
            if (url != null) hinhAnhSachRepository.save(HinhAnhSach.builder()
                    .duongDan(url).loaiHinhAnh(LoaiHinhAnh.BIA_TRUOC)
                    .thuTuHienThi(thuTu++).sach(savedSach).build());
        }
        if (anhBiaSau != null && !anhBiaSau.isBlank()) {
            String url = resolveImageUrl(anhBiaSau.trim(), imageMap);
            if (url != null) hinhAnhSachRepository.save(HinhAnhSach.builder()
                    .duongDan(url).loaiHinhAnh(LoaiHinhAnh.BIA_SAU)
                    .thuTuHienThi(thuTu++).sach(savedSach).build());
        }
        if (anhKhac != null && !anhKhac.isBlank()) {
            for (String part : anhKhac.split("\\|")) {
                String p = part.trim();
                if (p.isBlank()) continue;
                String url = resolveImageUrl(p, imageMap);
                if (url != null) hinhAnhSachRepository.save(HinhAnhSach.builder()
                        .duongDan(url).loaiHinhAnh(LoaiHinhAnh.KHAC)
                        .thuTuHienThi(thuTu++).sach(savedSach).build());
            }
        }
    }

    /**
     * Resolve chuỗi ở cột ảnh thành URL phục vụ được trên web.
     * 1. Nếu bắt đầu bằng http/https hoặc /api/v1/files/ → dùng trực tiếp.
     * 2. Nếu là tên file có trong imageMap → lưu vào uploads/ và trả URL mới.
     * 3. Không tìm thấy → trả null (bỏ qua ảnh đó).
     */
    private String resolveImageUrl(String value, Map<String, byte[]> imageMap) throws IOException {
        if (value == null || value.isBlank()) return null;

        if (value.startsWith("http://") || value.startsWith("https://")
                || value.startsWith("/api/v1/files/")) {
            return value;
        }

        String lowerValue = value.toLowerCase();
        byte[] imageBytes = imageMap.get(lowerValue);
        if (imageBytes == null) {
            String baseName = Paths.get(value).getFileName().toString().toLowerCase();
            imageBytes = imageMap.get(baseName);
        }

        if (imageBytes != null) {
            return saveImageBytes(imageBytes, value);
        }

        log.warn("Không tìm thấy ảnh '{}' trong ZIP, bỏ qua", value);
        return null;
    }

    /**
     * Lưu bytes ảnh vào uploads/ và trả về URL nội bộ.
     */
    private String saveImageBytes(byte[] bytes, String originalName) throws IOException {
        if (!Files.exists(UPLOAD_ROOT)) {
            Files.createDirectories(UPLOAD_ROOT);
        }
        String ext = "";
        int dotIdx = originalName.lastIndexOf('.');
        if (dotIdx >= 0) ext = originalName.substring(dotIdx).toLowerCase();

        String newFilename = UUID.randomUUID().toString() + ext;
        Files.write(UPLOAD_ROOT.resolve(newFilename), bytes);
        log.debug("Đã lưu ảnh import: uploads/{}", newFilename);
        return "/api/v1/files/" + newFilename;
    }

    /**
     * Sinh ISBN-13 hợp lệ về format (13 chữ số bắt đầu bằng 978)
     * để vượt qua @Pattern constraint trên entity Sach.
     */
    private String generateFallbackIsbn(int rowNum) {
        long ts = System.currentTimeMillis() % 10_000_000_000L; // 10 digits
        String candidate = String.format("978%010d", ts + rowNum);
        return candidate.substring(0, 13);
    }

    /**
     * Kiểm tra đuôi file có phải định dạng ảnh hỗ trợ không.
     */
    private boolean isImageFile(String lowerName) {
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".png") || lowerName.endsWith(".gif")
                || lowerName.endsWith(".webp") || lowerName.endsWith(".svg")
                || lowerName.endsWith(".bmp");
    }

    // ==========================
    // Utilities
    // ==========================
    private String cellStr(Row row, int i) {
        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null)
            return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    java.util.Date date = cell.getDateCellValue();
                    yield new java.text.SimpleDateFormat("dd/MM/yyyy").format(date);
                }
                double v = cell.getNumericCellValue();
                yield v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private boolean isEmptyRow(Row row, int colCount) {
        for (int i = 0; i < colCount; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }
}
