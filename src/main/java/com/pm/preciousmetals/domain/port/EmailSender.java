package com.pm.preciousmetals.domain.port;

public interface EmailSender {
    void send(String address, String title, String content);
}
