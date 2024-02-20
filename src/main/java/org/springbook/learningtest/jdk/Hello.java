package org.springbook.learningtest.jdk;

/**
 * 프록시를 적용할 타깃 클래스가 구현해야 할 인터페이스
 */
public interface Hello {
    String sayHello( String name );
    String sayHi( String name );
    String sayThankYou( String name );
}
