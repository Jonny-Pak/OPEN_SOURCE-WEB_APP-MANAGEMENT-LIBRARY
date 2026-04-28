package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {

    @Id
    @GeneratedValue
    @Column(name = "ma_nguoi_dung")
    private UUID maNguoiDung;

    @Column(name = "email")
    private String email;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "ho_dem")
    private String hoDem;

    @Column(name = "ten")
    private String ten;

    @Column(name = "mat_khau")
    private String matKhau;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro")
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiNguoiDung trangThai;

    public static Builder builder() {
        return new Builder();
    }

    public UUID getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(UUID maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHoDem() {
        return hoDem;
    }

    public void setHoDem(String hoDem) {
        this.hoDem = hoDem;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public TrangThaiNguoiDung getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiNguoiDung trangThai) {
        this.trangThai = trangThai;
    }

    public static class Builder {
        private final NguoiDung value = new NguoiDung();

        public Builder email(String email) {
            value.email = email;
            return this;
        }

        public Builder soDienThoai(String soDienThoai) {
            value.soDienThoai = soDienThoai;
            return this;
        }

        public Builder hoDem(String hoDem) {
            value.hoDem = hoDem;
            return this;
        }

        public Builder ten(String ten) {
            value.ten = ten;
            return this;
        }

        public NguoiDung build() {
            return value;
        }
    }
}