package com.destaxa.autorizador_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String paymentId;
    private double value;
    private String responseCode;
    private String authorizationCode;
    private String transactionDate;
    private String transactionHour;
}
