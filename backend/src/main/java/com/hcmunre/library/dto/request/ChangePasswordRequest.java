package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Mật khẩu cũ không được để trống")
    private String matKhauCu;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
             message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
    private String matKhauMoi;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String xacNhanMatKhau;
}

