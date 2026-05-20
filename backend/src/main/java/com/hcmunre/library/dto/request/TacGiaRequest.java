package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TacGiaRequest {

    @NotBlank(message = "Họ đệm không được để trống")
    private String hoDem;

    @NotBlank(message = "Tên tác giả không được để trống")
    private String ten;

    private String tieuSu;
    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate ngaySinh;
    private String quocTich;
    private String hinhAnh;
}
