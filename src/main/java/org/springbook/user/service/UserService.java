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

        /* 스프링이 제공하는 트랜잭션 추상화 방법 */
        // JDBC 트랜잭션 추상 오브젝트 생성. 사용한 DB의 DataSource를 주입받음
//        PlatformTransactionManager transactionManager = new DataSourceTransactionManager( dataSource ); // DataSourceTransactionManager: JDBC의 로컬 트랜잭션을 이용한 구현체
//        PlatformTransactionManager transactionManager = new JtaTransactionManager(); // JtaTransactionManager: JTA를 이용한 글로벌 트랜잭션을 이용한 구현체

        // DI로 받은 PlatformTransactionManager로 트랜잭션 시작 (멀티 스레드에도 OK)
        TransactionStatus status = this.transactionManager.getTransaction( new DefaultTransactionDefinition() );

        try {

            List< User > users = userDao.getAll();

            for ( User user : users ) {
                // 추상화. 레벨 업그레이드 가능한지 확인 후 -> 가능하면 레벨 업그레이드
                if ( userLevelUpgradePolicy.canUpgradeLevel( user ) ) {
                    upgradeLevel( user );
                }
            }

            // 트랜잭션 커밋
            this.transactionManager.commit( status );
        }
        catch ( Exception e ) {
            // 트랜잭션 롤백
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

