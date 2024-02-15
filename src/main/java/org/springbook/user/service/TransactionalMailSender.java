package org.springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 트랜잭션을 적용한 메일 전송 오브젝트
 */
public class TransactionalMailSender implements MailSender {
    private MailSender mailSender;
    private List<SimpleMailMessage> messageQueue = new ArrayList<>(); // 전송 요청을 모아둘 메시지 리스트

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    /**
     * 실제 메일 전송 대신에 큐에 메시지 저장
     * @param simpleMessage the message to send
     * @throws MailException
     */
    @Override
    public void send( SimpleMailMessage simpleMessage ) throws MailException {
        messageQueue.add( simpleMessage );
    }

    @Override
    public void send( SimpleMailMessage... simpleMessages ) throws MailException {
        for ( SimpleMailMessage message : simpleMessages ) {
            send( message );
        }
    }

    /**
     * 트랜잭션 성공시 큐에 모인 메시지들을 실제 메일 전송
     */
    public void sendAll(){
        for ( SimpleMailMessage message : messageQueue ) {
            mailSender.send( message );
        }

        messageQueue.clear();

    }

    /**
     * 예외 발생시 호출될 메소드. 메일 전송 취소
     */
    public void clear(){
        messageQueue.clear();
    }
}
