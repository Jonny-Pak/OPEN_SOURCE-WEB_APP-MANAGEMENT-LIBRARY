package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.GioiTinh;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTaoNguoiDungRequest {

    @NotBlank(message = "Họ đệm không được để trống")
    private String hoDem;

    @NotBlank(message = "Tên không được để trống")
    private String ten;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    /**
     * Mật khẩu do admin đặt. Không bắt buộc độ phức tạp vì admin quản lý.
     * Nếu để trống, backend sẽ dùng mật khẩu mặc định "Password@123".
     */
    private String matKhau;

    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    private String soDienThoai;

    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải gồm 12 chữ số")
    private String cccd;

    private LocalDate ngaySinh;

    private GioiTinh gioiTinh;

    private VaiTro vaiTro;

    private TrangThaiNguoiDung trangThai;
}
