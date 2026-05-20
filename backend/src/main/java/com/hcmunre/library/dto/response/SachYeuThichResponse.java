package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SachYeuThichResponse {
    private Long id;
    private Long maSach;
    private String tenSach;
    private String maIsbn;
}
