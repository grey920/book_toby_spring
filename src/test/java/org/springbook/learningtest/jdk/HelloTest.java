package org.springbook.learningtest.jdk;


import org.junit.jupiter.api.Test;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.assertj.core.api.Assertions.assertThat;

class HelloTest {

    @Test
    public void simpleProxy(){
        Hello hello = new HelloTarget(); // 타깃은 인터페이스를 통해 접근하는 것이 좋다.
        assertThat( hello.sayHello( "Grey" ) ).isEqualTo( "Hello Grey" );
        assertThat( hello.sayHi( "Grey" ) ).isEqualTo( "Hi Grey" );
        assertThat( hello.sayThankYou( "Grey" ) ).isEqualTo( "Thank You Grey" );
    }

    @Test
    public void uppercaseProxy(){
        Hello hello = new HelloUppercase( new HelloTarget() );
        assertThat( hello.sayHello( "Grey" ) ).isEqualTo( "HELLO GREY" );
        assertThat( hello.sayHi( "Grey" ) ).isEqualTo( "HI GREY" );
        assertThat( hello.sayThankYou( "Grey" ) ).isEqualTo( "THANK YOU GREY" );
    }

    @Test
    public void dynamicProxy(){
        Hello proxiedHello = (Hello) newProxyInstance(
                getClass().getClassLoader(), // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{ Hello.class }, // 구현할 인터페이스
                new UppercaseHandler( new HelloTarget() ) ); // 부가기능과 위임 코드를 담은 InvocationHandler

        assertThat( proxiedHello.sayHello( "Grey" ) ).isEqualTo( "HELLO GREY" );
        assertThat( proxiedHello.sayHi( "Grey" ) ).isEqualTo( "HI GREY" );
        assertThat( proxiedHello.sayThankYou( "Grey" ) ).isEqualTo( "THANK YOU GREY" );
    }

}