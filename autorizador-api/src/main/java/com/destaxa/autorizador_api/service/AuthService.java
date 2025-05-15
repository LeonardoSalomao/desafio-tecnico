package com.destaxa.autorizador_api.service;

import com.destaxa.autorizador_api.dto.AuthRequest;
import com.destaxa.autorizador_api.dto.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthService {
    private static final String HOST = "localhost";
    private static final int PORT = 8583;

    public AuthResponse authorize(AuthRequest request) {
        try (Socket socket = new Socket(HOST, PORT)) {
            long valorCentavos = (long) (request.getValue() * 100);

            String isoMessage = String.format("0200|%s|%d|%s|%d|%s|%02d|%02d",
                    request.getCardNumber(),
                    valorCentavos,
                    request.getInstallments() > 1 ? "003001" : "003000",
                    request.getInstallments(),
                    request.getCvv(),
                    request.getExpMonth(),
                    request.getExpYear());

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(isoMessage.getBytes());
            out.flush();

            byte[] buffer = new byte[1024];
            int length = in.read(buffer);
            if (length == -1) {
                throw new RuntimeException("Timeout sem resposta do autorizador.");
            }

            String jsonResponse = new String(buffer, 0, length);
            return new ObjectMapper().readValue(jsonResponse, AuthResponse.class);

        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            return AuthResponse.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .value(request.getValue())
                    .responseCode("TIMEOUT")
                    .authorizationCode(null)
                    .transactionDate(now.format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .transactionHour(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .build();
        }
    }
}
