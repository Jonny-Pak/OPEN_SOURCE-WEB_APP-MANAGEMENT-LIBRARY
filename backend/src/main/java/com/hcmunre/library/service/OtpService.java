package com.hcmunre.library.service;

public interface OtpService {
    String taoOtp(String email);
    boolean xacMinhOtp(String email, String otp);
    boolean daHetHan(String email);
    void donDepOtpHetHan();
}
