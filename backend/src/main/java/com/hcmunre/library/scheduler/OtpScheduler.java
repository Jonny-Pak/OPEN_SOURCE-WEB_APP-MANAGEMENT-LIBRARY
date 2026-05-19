package com.hcmunre.library.scheduler;

import com.hcmunre.library.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OtpScheduler {
    private final OtpService otpService;

    @Scheduled(fixedDelay = 300000)
    public void donDepOtpHetHan(){
        otpService.donDepOtpHetHan();
    }
}
