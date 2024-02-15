package org.springbook.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springbook.user.dao.UserDao;
import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    UserLevelUpgradePolicy userLevelUpgradePolicy;
    UserDao userDao;

    private PlatformTransactionManager transactionManager;

    private MailSender mailSender;

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    public void setTransactionManager( PlatformTransactionManager transactionManager ) {
        this.transactionManager = transactionManager;
    }
    public void setUserLevelUpgradePolicy( UserLevelUpgradePolicy userLevelUpgradePolicy ) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    public void add( User user ) {
        if ( user.getLevel() == null ) {
            user.setLevel( Level.BASIC );
        }
        userDao.add( user );
    }

    public void upgradeLevels() throws Exception {
        TransactionStatus status = this.transactionManager.getTransaction( new DefaultTransactionDefinition() );

        try {

            // 실제 비즈니스 로직 ------------------>
            upgradeLevelInternal();
            // <-----------------------------------

            this.transactionManager.commit( status );
        }
        catch ( Exception e ) {
            this.transactionManager.rollback( status );
            throw e;
        }
    }

    protected void upgradeLevel( User user ) {
        // 사용자 레벨을 다음 레벨로 변경
        userLevelUpgradePolicy.upgradeLevel( user );

        // DB 업데이트
        userDao.update( user );

        // 업그레이드 후 메일 발송
        sendUpgradeEmail( user );
    }

    /**
     * 사용자 레벨 업그레이드 로직 분리
     */
    private void upgradeLevelInternal() {
        List< User > users = userDao.getAll();

        for ( User user : users ) {
            if ( userLevelUpgradePolicy.canUpgradeLevel( user ) ) {
                upgradeLevel( user );
            }
        }
    }

    private void sendUpgradeEmail( User user ) {

        // MailMessage 구현 클래스의 오브젝트 생성
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo( user.getEmail() );
        mailMessage.setFrom( "useradmin@ksug.org" );
        mailMessage.setSubject( "Upgrade 안내" );
        mailMessage.setText( user.getName() + "님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다." );

        this.mailSender.send( mailMessage );
    }


    /**
     * 트랜잭션 테스트용 클래스
     */
    static class TestUserService extends UserService {
        private String id;

        TestUserService( String id ) {
            this.id = id;
        }

        protected void upgradeLevel( User user ) {
            if ( user.getId().equals( this.id ) ) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel( user );
        }
    }

    /**
     * 테스트용 예외 클래스
     */
    static class TestUserServiceException extends RuntimeException {
    }


}

