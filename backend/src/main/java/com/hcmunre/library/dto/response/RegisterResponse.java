package com.hcmunre.library.dto.response;

import java.util.UUID;

public class RegisterResponse {
    private UUID maNguoiDung;
    private String email;
    private String soDienThoai;
    private String hoDem;
    private String ten;
    private String message;

    public RegisterResponse(UUID maNguoiDung, String email, String soDienThoai, String hoDem, String ten, String message) {
        this.maNguoiDung = maNguoiDung;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.hoDem = hoDem;
        this.ten = ten;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
