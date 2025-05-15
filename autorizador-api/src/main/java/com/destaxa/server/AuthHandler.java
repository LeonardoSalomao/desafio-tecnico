package com.destaxa.server;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class AuthHandler implements Runnable {
    private final Socket socket;

    public AuthHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int length = in.read(buffer);
            if (length == -1) return;

            String request = new String(buffer, 0, length);
            System.out.println("\n=== REQUISIÇÃO RECEBIDA ===");
            System.out.println("Mensagem ISO8583: " + request);

            String[] parts = request.split("\\|");
            if (!parts[0].equals("0200")) {
                System.out.println("ERRO: Código de mensagem inválido");
                return;
            }

            double valorEmCentavos = Double.parseDouble(parts[2]);
            double valor = valorEmCentavos / 100;

            if (valor <= 0) {
                System.out.println("\n=== DETALHES DA TRANSAÇÃO ===");
                System.out.printf("Valor: R$ %.2f%n", valor);
                System.out.println("Status: NEGADO (valor não aceito)");
                System.out.println("Código: 051");
                System.out.println("Autorização: N/A");

                LocalDateTime now = LocalDateTime.now();
                String jsonResponse = String.format(
                        "{\"paymentId\":\"%s\",\"value\":%.2f,\"responseCode\":\"%s\"," +
                                "\"authorizationCode\":null,\"transactionDate\":\"%s\",\"transactionHour\":\"%s\"}",
                        UUID.randomUUID().toString(),
                        valor,
                        "051",
                        now.format(DateTimeFormatter.ofPattern("yy-MM-dd")),
                        now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                ).replace(".", ",");

                out.write(jsonResponse.getBytes());
                out.flush();
                return;
            }

            boolean isPar;
            if (valor == Math.floor(valor)) {
                isPar = ((int)valor % 2 == 0);
            } else {
                isPar = (valorEmCentavos % 2 == 0);
            }

            if (valor > 1000.0) {
                System.out.println("STATUS: TIMEOUT (valor > R$ 1000)");
                return;
            }

            String responseCode = isPar ? "000" : "051";
            String authCode = isPar ? gerarAuthCode() : "";
            LocalDateTime now = LocalDateTime.now();

            System.out.println("\n=== DETALHES DA TRANSAÇÃO ===");
            System.out.printf("Valor: R$ %.2f%n", valor);
            System.out.println("Status: " + (isPar ? "APROVADO" : "NEGADO"));
            System.out.println("Código: " + responseCode);
            System.out.println("Autorização: " + (authCode.isEmpty() ? "N/A" : authCode));
            System.out.println("Data: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("Hora: " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            System.out.println("=============================");

            String jsonResponse = String.format(
                    "{\"paymentId\":\"%s\",\"value\":%.2f,\"responseCode\":\"%s\"," +
                            "\"authorizationCode\":\"%s\",\"transactionDate\":\"%s\",\"transactionHour\":\"%s\"}",
                    UUID.randomUUID().toString(),
                    valor,
                    responseCode,
                    authCode,
                    now.format(DateTimeFormatter.ofPattern("yy-MM-dd")),
                    now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ).replace(".", ",");

            out.write(jsonResponse.getBytes());
            out.flush();

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception ignore) {}
        }
    }

    private String gerarAuthCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
