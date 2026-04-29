package com.hcmunre.library.security;

import com.hcmunre.library.entity.NguoiDung;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Lớp đại diện cho thông tin người dùng trong hệ thống Spring Security.
 * Bọc (wrap) Entity NguoiDung để Spring Security có thể sử dụng
 * trong quá trình xác thực và phân quyền.
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * Entity NguoiDung chứa toàn bộ thông tin người dùng từ database.
     */
    private final NguoiDung nguoiDung;

    /**
     * Trả về danh sách quyền (GrantedAuthority) của người dùng.
     * Mỗi người dùng có đúng 1 vai trò, được prefix "ROLE_" phía trước.
     * Ví dụ: QUAN_TRI_VIEN -> ROLE_QUAN_TRI_VIEN
     *
     * @return danh sách quyền hạn
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Chuyển đổi VaiTro enum thành GrantedAuthority với prefix "ROLE_"
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro().name())
        );
    }

    /**
     * Trả về mật khẩu đã mã hóa (BCrypt) của người dùng.
     *
     * @return mật khẩu đã hash
     */
    @Override
    public String getPassword() {
        return nguoiDung.getMatKhau();
    }

    /**
     * Trả về tên đăng nhập (email) của người dùng.
     * Hệ thống sử dụng email làm tên đăng nhập duy nhất.
     *
     * @return email người dùng
     */
    @Override
    public String getUsername() {
        return nguoiDung.getEmail();
    }

    /**
     * Kiểm tra tài khoản còn hiệu lực hay không.
     * Trả về true nếu trạng thái là HOAT_DONG.
     *
     * @return true nếu tài khoản đang hoạt động
     */
    @Override
    public boolean isAccountNonLocked() {
        return nguoiDung.getTrangThai() == com.hcmunre.library.enums.TrangThaiNguoiDung.HOAT_DONG;
    }
}
