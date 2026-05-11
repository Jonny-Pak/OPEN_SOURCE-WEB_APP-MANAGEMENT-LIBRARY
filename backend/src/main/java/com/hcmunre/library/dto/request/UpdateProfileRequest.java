package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Họ đệm không được để trống")
    private String hoDem;

    @NotBlank(message = "Tên không được để trống")
    private String ten;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    private String soDienThoai;
}

