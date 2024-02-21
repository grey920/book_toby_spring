package org.springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionHandler implements InvocationHandler {

    // 부가 기능을 제공할 타깃 오브젝트.
    private Object target;

    // 트랜잭션 기능을 제공하는데 필요한 트랜잭션 매니저
    private PlatformTransactionManager transactionManager;

    // 트랜잭션을 적용할 메소드 이름 패턴
    private String pattern;

    public void setTarget( Object target ) {
        this.target = target;
    }

    public void setTransactionManager( PlatformTransactionManager transactionManager ) {
        this.transactionManager = transactionManager;
    }

    public void setPattern( String pattern ) {
        this.pattern = pattern;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        // 트랜잭션 적용 대상 메소드를 선별해서 트랜잭션 경계설정 기능을 부여
        if ( method.getName().startsWith( pattern ) ) {
            return invokeInTransaction( method, args );
        }
        else {
            return method.invoke( target, args );
        }
    }

    private Object invokeInTransaction( Method method, Object[] args ) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction( new DefaultTransactionDefinition() );

        try {
            // 트랜잭션 시작하고 타깃 오브젝트의 메소드를 호출한다. -> 정상 종료시 트랜잭션 커밋
            Object ret = method.invoke( target, args );
            this.transactionManager.commit( status );
            return ret;
        }
        // 예외 발생시 트랜잭션 롤백
        // 리플렉션 메소드인 Method.invoke()로 호출했을때 타깃에서 발생한 예외는 InvocationTargetException으로 한 번 포장돼서 전달됨
        // -> 일단 InvocationTargetException로 받고 getTargetException()으로 중첩되어 있던 예외를 가져온다.
        catch ( InvocationTargetException e ) {
            this.transactionManager.rollback( status );
            throw e.getTargetException();
        }
    }
}
