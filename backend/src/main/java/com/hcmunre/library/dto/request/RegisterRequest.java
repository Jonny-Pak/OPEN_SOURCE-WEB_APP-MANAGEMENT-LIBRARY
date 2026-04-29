package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu đăng ký tài khoản mới.
 * Validate đầy đủ các trường: họ đệm, tên, email, mật khẩu, số điện thoại.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Họ đệm - bắt buộc */
    @NotBlank(message = "Họ đệm không được để trống")
    private String hoDem;

    /** Tên - bắt buộc */
    @NotBlank(message = "Tên không được để trống")
    private String ten;

    /** Email - bắt buộc, đúng định dạng */
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    /** Mật khẩu - bắt buộc, tối thiểu 6 ký tự */
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    /** Số điện thoại - bắt buộc, đúng định dạng VN (10 số, bắt đầu bằng 0) */
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    private String soDienThoai;
}
