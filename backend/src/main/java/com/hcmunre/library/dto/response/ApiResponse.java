package com.hcmunre.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private String message;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
