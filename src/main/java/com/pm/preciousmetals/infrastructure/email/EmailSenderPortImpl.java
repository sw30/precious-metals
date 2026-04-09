package com.pm.preciousmetals.infrastructure.email;

import com.pm.preciousmetals.domain.port.EmailSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSenderPortImpl implements EmailSenderPort {
    @Override
    public void send(String address, String title, String content) {
      log.info("Email sent to {} with title: {}, content: {}", address, title, content);
    }
}

