package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.response.ImportExcelResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.GioiTinh;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.service.ImportExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExcelServiceImplement implements ImportExcelService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Password@123";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) continue;

                tongSoDong++;
                try {
                    nguoiDungRepository.save(parseRow(row));
                    thanhCong++;
                } catch (LibraryException e) {
                    danhSachLoi.add("Dòng " + (i + 1) + ": " + e.getErrorCode().getMessage());
                }
            }
        } catch (LibraryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi đọc file Excel: {}", e.getMessage(), e);
            throw new LibraryException(ErrorCode.LOI_DOC_FILE_EXCEL);
        }

        log.info("Import Excel hoàn tất: {}/{} thành công, {} lỗi",
                thanhCong, tongSoDong, danhSachLoi.size());

        return ImportExcelResponse.builder()
                .tongSoDong(tongSoDong)
                .thanhCong(thanhCong)
                .thatBai(danhSachLoi.size())
                .danhSachLoi(danhSachLoi)
                .build();
    }

    private NguoiDung parseRow(Row row) {
        String mssv = cellStr(row, 1);
        String hoTenStr = cellStr(row, 2);
        String gtStr = cellStr(row, 3);
        String nsStr = cellStr(row, 4);

        if (mssv == null || mssv.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_EMAIL); // Thiếu MSSV nên thiếu email
        }
        if (hoTenStr == null || hoTenStr.isBlank()) {
            throw new LibraryException(ErrorCode.EXCEL_THIEU_TEN);
        }

        // Tách họ tên thành hoDem và ten
        hoTenStr = hoTenStr.trim();
        int lastSpaceIdx = hoTenStr.lastIndexOf(' ');
        String hoDem = "";
        String ten = "";
        if (lastSpaceIdx > 0) {
            hoDem = hoTenStr.substring(0, lastSpaceIdx).trim();
            ten = hoTenStr.substring(lastSpaceIdx + 1).trim();
        } else {
            hoDem = "Sinh viên";
            ten = hoTenStr;
        }

        // Sinh email tự động: mssv@sv.hcmunre.edu.vn
        String email = mssv.trim().toLowerCase() + "@sv.hcmunre.edu.vn";

        if (nguoiDungRepository.existsByEmail(email)) {
            throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
        }

        // Tạo số điện thoại ảo duy nhất dựa trên MSSV bắt đầu bằng 0 và đủ 10 số để thoả mãn regex validation
        String mssvClean = mssv.replaceAll("\\D", "");
        String sdtSuffix = mssvClean;
        if (sdtSuffix.length() > 9) {
            sdtSuffix = sdtSuffix.substring(sdtSuffix.length() - 9);
        } else {
            while (sdtSuffix.length() < 9) {
                sdtSuffix = sdtSuffix + "0";
            }
        }
        String sdt = "0" + sdtSuffix;

        if (nguoiDungRepository.existsBySoDienThoai(sdt)) {
            throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
        }

        LocalDate ngaySinh = null;
        if (nsStr != null && !nsStr.isBlank()) {
            try {
                ngaySinh = LocalDate.parse(nsStr.trim(), DATE_FMT);
            } catch (DateTimeParseException e) {
                throw new LibraryException(ErrorCode.EXCEL_NGAY_SINH_SAI_FORMAT);
            }
        }

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

        return NguoiDung.builder()
                .hoDem(hoDem)
                .ten(ten)
                .email(email)
                .soDienThoai(sdt)
                .matKhau(passwordEncoder.encode(DEFAULT_PASSWORD))
                .ngaySinh(ngaySinh)
                .gioiTinh(gioiTinh)
                .vaiTro(VaiTro.DOC_GIA)
                .trangThai(TrangThaiNguoiDung.CHUA_KICH_HOAT)
                .build();
    }

    private String cellStr(Row row, int i) {
        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
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

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < 4; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}
