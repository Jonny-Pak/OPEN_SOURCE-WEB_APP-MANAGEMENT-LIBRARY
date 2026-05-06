package com.hcmunre.library.service;

import com.hcmunre.library.entity.TheLoai;
import java.util.List;

public interface TheLoaiService {
    // Queries
    List<TheLoai> getAllTheLoai();
    List<TheLoai> searchTheLoai(String keyword);
    TheLoai getTheLoaiById(Long id);

    // Commands
    TheLoai createTheLoai(TheLoai theLoai);
    TheLoai updateTheLoai(Long id, TheLoai theLoai);
    void deleteTheLoai(Long id);
}
