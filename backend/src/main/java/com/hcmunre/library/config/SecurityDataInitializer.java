package com.hcmunre.library.config;

import com.hcmunre.library_backend.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.repository.NguoiDungRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityDataInitializer implements ApplicationRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final DefaultUserSecurityProperties defaultUserSecurityProperties;
    private final PasswordEncoder passwordEncoder;

    public SecurityDataInitializer(NguoiDungRepository nguoiDungRepository,
            DefaultUserSecurityProperties defaultUserSecurityProperties,
            PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.defaultUserSecurityProperties = defaultUserSecurityProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String username = defaultUserSecurityProperties.getUsername();
        VaiTro role = parseRole(defaultUserSecurityProperties.getRole());

        NguoiDung user = nguoiDungRepository.findByEmailOrSoDienThoai(username, username)
                .orElseGet(() -> NguoiDung.builder()
                        .email(username)
                        .soDienThoai(username)
                        .hoDem("System")
                        .ten("Admin")
                        .build());

        user.setEmail(username);
        user.setMatKhau(passwordEncoder.encode(defaultUserSecurityProperties.getPassword()));
        user.setVaiTro(role);
        user.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);

        nguoiDungRepository.save(user);
    }

    private VaiTro parseRole(String role) {
        if (role == null || role.isBlank()) {
            return VaiTro.THU_THU;
        }
        try {
            return VaiTro.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return VaiTro.THU_THU;
        }
    }
}
