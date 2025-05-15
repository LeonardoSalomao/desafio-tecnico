package com.destaxa.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthServer {
        public static void main(String[] args) {
            try (ServerSocket serverSocket = new ServerSocket(8583)) {
                System.out.println("Servidor autorizador iniciado na porta 8583");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new AuthHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                System.err.println("Erro no servidor: " + e.getMessage());
            }
        }
    }