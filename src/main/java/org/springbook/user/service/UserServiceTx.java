package org.springbook.user.service;

import org.springbook.user.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 이 클래스는 트랜잭션 경계설정 작업만을 책임진다.
 * 비즈니스 로직은 다른 구현 클래스에 위임한다.
 */
public class UserServiceTx implements UserService{
    // 타깃 오브젝트
    UserService userService;
    PlatformTransactionManager transactionManager;

    public void setTransactionManager( PlatformTransactionManager transactionManager ) {
        this.transactionManager = transactionManager;
    }

    /**
     * UserService의 비즈니스 로직을 구현한 클래스를 DI 받는다.
     * @param userService
     */
    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    /* 메소드 구현과 위임 */
    @Override
    public void add( User user ) {
        userService.add( user );
    }

    @Override
    public void upgradeLevels() {
        // 부가기능
        TransactionStatus status = this.transactionManager.getTransaction( new DefaultTransactionDefinition() );

        try{

            // 위임
            userService.upgradeLevels();

            // 부가기능 ---------------->
            this.transactionManager.commit( status );
        }
        catch ( RuntimeException e ) {
            this.transactionManager.rollback( status );
            throw e;
        } // <-------------------------
    }
}
