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
        if ( ret instanceof String && method.getName().startsWith( "say" ) ) {
            return ( ( String ) ret ).toUpperCase();
        }
        // 조건이 일치하지 않으면 타깃 오브젝트 결과를 그대로 리턴
        else {
            return ret;
        }

    }
}
