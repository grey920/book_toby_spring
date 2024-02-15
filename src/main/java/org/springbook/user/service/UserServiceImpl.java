package org.springbook.user.service;

import org.springbook.user.dao.UserDao;
import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserServiceImpl implements UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    UserLevelUpgradePolicy userLevelUpgradePolicy;
    UserDao userDao;


    private MailSender mailSender;

    public void setUserLevelUpgradePolicy( UserLevelUpgradePolicy userLevelUpgradePolicy ) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
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

    public void upgradeLevels() {

        List< User > users = userDao.getAll();

        for ( User user : users ) {
            if ( userLevelUpgradePolicy.canUpgradeLevel( user ) ) {
                upgradeLevel( user );
            }
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
    static class TestUserService extends UserServiceImpl {
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

