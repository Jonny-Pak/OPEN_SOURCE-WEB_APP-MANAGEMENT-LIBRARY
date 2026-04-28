package com.hcmunre.library.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UUID maNguoiDung;
    private String email;
    private String soDienThoai;
    private String hoDem;
    private String ten;
    private String message;
}
