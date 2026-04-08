package com.pm.preciousmetals.infrastructure.email;

import com.pm.preciousmetals.domain.port.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSenderImpl implements EmailSender {
    @Override
    public void send(String address, String title, String content) {
      log.info("Email sent to {} with title: {}, content: {}", address, title, content);
    }
}
