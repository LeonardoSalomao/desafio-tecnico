package com.destaxa.autorizador_api.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String externalId;
    private double value;
    private String cardNumber;
    private int installments;
    private String cvv;
    private int expMonth;
    private int expYear;
    private String holderName;
}
