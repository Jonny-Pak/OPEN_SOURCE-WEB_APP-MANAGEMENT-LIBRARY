package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TacGiaResponse {
    private Long maTacGia;
    private String hoDem;
    private String ten;
    private String tieuSu;
    private String hinhAnh;
}
