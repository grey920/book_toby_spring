package org.springbook.learningtest.jdk;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;


public class ReflectionTest {
    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        // length()
        assertThat( name.length() ).isEqualTo( 6 );

        Method lengthMethod = String.class.getMethod( "length" );
        assertThat( lengthMethod.invoke( name ) ).isEqualTo( 6 );

        // charAt()
        assertThat( name.charAt( 0 ) ).isEqualTo( 'S' );

        Method charAtMethod = String.class.getMethod( "charAt", int.class );
        assertThat( charAtMethod.invoke( name, 0 ) ).isEqualTo( 'S' );
    }
}
