package org.springbook.learningtest.jdk;

/**
 * [데코레이터] HelloTarget 클래스의 메소드를 대문자로 바꾸어 반환하는 프록시 클래스
 *
 * 문제점
 * 1. Hello 인터페이스의 모든 메소드를 구현해서 위임하도록 코드를 만들어야 한다.
 * 2. 부가기능인 대문자로 바꾸는 기능을 적용하고 싶은 메소드마다 중복해서 적용해야 한다.
 */
public class HelloUppercase implements Hello{
    // 위임할 타깃 오브젝트. 다른 프록시도 추가할 수 있으므로 인터페이스로 접근한다.
    Hello hello;

    public HelloUppercase( Hello hello ) {
        this.hello = hello;
    }

    @Override
    public String sayHello( String name ) {
        return hello.sayHello( name ).toUpperCase(); // 위임(hello.sayHello) + 부가기능(toUpperCase)
    }

    @Override
    public String sayHi( String name ) {
        return hello.sayHi( name ).toUpperCase();
    }

    @Override
    public String sayThankYou( String name ) {
        return hello.sayThankYou( name ).toUpperCase();
    }
}
