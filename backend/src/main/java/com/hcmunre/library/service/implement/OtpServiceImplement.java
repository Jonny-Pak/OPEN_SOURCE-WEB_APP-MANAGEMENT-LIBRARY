package com.hcmunre.library.service.implement;

import com.hcmunre.library.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpServiceImplement implements OtpService {
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 15;

    @Override
    public String taoOtp(String email) {
        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(100000 + random.nextInt(900000));
        otpStore.put(email.toLowerCase(), new OtpData(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)));
        log.info("Đã tạo OTP cho email: {}", email);

        return otp;
    }

    @Override
    public boolean xacMinhOtp(String email, String otp) {
        OtpData data = otpStore.get(email.toLowerCase());

        if(data == null){
            return false;
        }

        if(LocalDateTime.now().isAfter(data.expiry())){
            otpStore.remove(email.toLowerCase());
            return false;
        }

        if(!data.otp().equals(otp)){
            return false;
        }

        otpStore.remove(email.toLowerCase());
        return true;
    }

    @Override
    public boolean daHetHan(String email) {
        OtpData data = otpStore.get(email.toLowerCase());
        if(data == null) return false;
        return LocalDateTime.now().isAfter(data.expiry());
    }

    @Override
    public void donDepOtpHetHan(){
        LocalDateTime now = LocalDateTime.now();
        int sizeBefore = otpStore.size();

        otpStore.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiry()));
        int removed = sizeBefore - otpStore.size();
        if(removed > 0){
            log.info("Đã dọn dep {} OTP hết hạn", removed);
        }
    }

    private record OtpData(String otp, LocalDateTime expiry){}
}
