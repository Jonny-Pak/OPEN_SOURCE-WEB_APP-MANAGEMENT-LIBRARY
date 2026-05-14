package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LienHeRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;

    @NotBlank(message = "Nội dung tên không được để trống")
    private String noiDung;
}
