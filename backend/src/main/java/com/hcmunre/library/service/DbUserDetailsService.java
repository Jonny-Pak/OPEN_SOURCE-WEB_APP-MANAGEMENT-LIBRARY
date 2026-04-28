package com.hcmunre.library.service;

import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.repository.NguoiDungRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public DbUserDetailsService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository
                .findByEmailOrSoDienThoai(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        boolean enabled = nguoiDung.getTrangThai() == null || nguoiDung.getTrangThai() == TrangThaiNguoiDung.HOAT_DONG;
        String role = nguoiDung.getVaiTro() != null ? nguoiDung.getVaiTro().name() : "DOC_GIA";

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return User.builder()
                .username(nguoiDung.getEmail() != null ? nguoiDung.getEmail() : username)
                .password(nguoiDung.getMatKhau())
                .authorities(authorities)
                .disabled(!enabled)
                .build();
    }
}
