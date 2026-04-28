package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NhaXuatBanRequest {

    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    private String tenNhaXuatBan;

    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3578]\\d{8}$", message = "Số điện thoại không đúng định dạng (ví dụ: 0912345678 hoặc +84912345678)")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
}
