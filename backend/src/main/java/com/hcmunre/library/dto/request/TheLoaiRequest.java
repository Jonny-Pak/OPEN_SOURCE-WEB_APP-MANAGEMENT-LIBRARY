package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TheLoaiRequest {
    @NotBlank(message = "Tên thể loại không được để trống")
    private String tenTheLoai;
    
    private String moTa;
}
