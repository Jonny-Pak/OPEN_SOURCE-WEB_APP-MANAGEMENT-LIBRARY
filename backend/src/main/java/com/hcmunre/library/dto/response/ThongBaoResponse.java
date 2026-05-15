package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.LoaiThongBao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ThongBaoResponse {
    private UUID maThongBao;
    private String tieuDe;
    private String noiDung;
    private LoaiThongBao loaiThongBao;
    private boolean daDoc;
    private LocalDateTime ngayTao;
}
