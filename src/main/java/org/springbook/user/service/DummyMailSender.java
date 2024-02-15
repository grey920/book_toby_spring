package org.springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * 테스트용 메일 전송 오브젝트
 */
public class DummyMailSender implements MailSender {
    @Override
    public void send( SimpleMailMessage simpleMessage ) throws MailException {
        System.out.println( "DummyMailSender called!!!");
        // 아무것도 하지 않는다.
    }

    @Override
    public void send( SimpleMailMessage... simpleMessages ) throws MailException {
        // 아무것도 하지 않는다.
    }
}
