package com.hcmunre.library.security;

import com.hcmunre.library.entity.NguoiDung;

import com.hcmunre.library.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service tải thông tin người dùng từ database cho Spring Security.
 * Được Spring Security gọi tự động trong quá trình xác thực
 * để lấy thông tin người dùng dựa trên email (tên đăng nhập).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    /**
     * Tải thông tin người dùng từ database theo email.
     * Phương thức này được Spring Security gọi khi xác thực.
     *
     * @param email email (tên đăng nhập) của người dùng
     * @return đối tượng UserDetails chứa thông tin người dùng
     * @throws UsernameNotFoundException nếu không tìm thấy người dùng với email đã
     *                                   cho
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm người dùng trong database theo email
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với email: " + email));

        if (nguoiDung.getNgayXoa() != null) {
            throw new UsernameNotFoundException("Tài khoản đã bị xóa");
        }

        // Bọc entity NguoiDung vào CustomUserDetails để Spring Security sử dụng
        return new CustomUserDetails(nguoiDung);
    }
}
