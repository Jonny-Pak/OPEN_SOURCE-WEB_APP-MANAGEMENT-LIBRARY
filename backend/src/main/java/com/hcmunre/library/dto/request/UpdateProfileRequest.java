package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.GioiTinh;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Họ đệm không được để trống")
    private String hoDem;

    @NotBlank(message = "Tên không được để trống")
    private String ten;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    private String soDienThoai;

    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate ngaySinh;

    private GioiTinh gioiTinh;

    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải bao gồm 12 chữ số")
    private String cccd;

    private String diaChi;
}
