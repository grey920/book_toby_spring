package org.springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {
    Object target;

    /**
     * 어떤 종류의 인터페이스를 구현한 타깃이 들어와도 적용 가능하도록 Object 타입을 받는다
     * @param target
     */
    public UppercaseHandler( Object target ) {
        this.target = target;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        Object ret = method.invoke( target, args );
        // 호출한 메소드의 리턴 타입이 String인 경우에만 대문자 변경 기능 적용
        if ( ret instanceof String ) {
            return ( ( String ) ret ).toUpperCase();
        }
        else {
            return ret;
        }

    }
}
