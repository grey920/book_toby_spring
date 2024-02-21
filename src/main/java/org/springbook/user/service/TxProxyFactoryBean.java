package org.springbook.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean< Object > { // 범용적으로 사용하기 위해 Object 타입으로 설정
    /* TransactionHandler를 생성할 때 필요한 정보 */
    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;

    Class< ? > serviceInterface; // 동적 프록시를 생성할 때 필요. UserService 외의 인터페이스를 가진 타깃에도 적용할 수 있다.

    public void setTarget( Object target ) {
        this.target = target;
    }

    public void setTransactionManager( PlatformTransactionManager transactionManager ) {
        this.transactionManager = transactionManager;
    }

    public void setPattern( String pattern ) {
        this.pattern = pattern;
    }

    public void setServiceInterface( Class< ? > serviceInterface ) {
        this.serviceInterface = serviceInterface;
    }

    /**
     * FactoryBean 인터페이스 구현 메소드
     * DI 받은 정보를 이용해서 TransactionHandler를 사용하는 다이내믹 프록시를 생성한다.
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget( target );
        txHandler.setTransactionManager( transactionManager );
        txHandler.setPattern( pattern );

        return Proxy.newProxyInstance( getClass().getClassLoader(), new Class[]{ serviceInterface }, txHandler );
    }

    /**
     * 팩토리 빈이 생성하는 오브젝트의 타입은 DI 받은 인터페이스 타입에 따라 달라진다.
     * 따라서 다양한 타입의 오브젝트를 DI 받을 수 있다. ( 다양한 타입에 재사용)
     * @return
     */
    @Override
    public Class< ? > getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false; // 싱글톤 빈이 아니라는 뜻이 아니라 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 뜻이다.
    }
}
