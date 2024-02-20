package org.springbook.learningtest.jdk;


import org.junit.jupiter.api.Test;

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

}