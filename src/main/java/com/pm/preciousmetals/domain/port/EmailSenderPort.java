package com.pm.preciousmetals.domain.port;

public interface EmailSenderPort {
    void send(String address, String title, String content);
}

