package org.springbook.learningtest.factorybean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(locations = "/FactoryBeanTest-context.xml")
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        // message 빈의 타입이 무엇인지 코드 상에서는 알 수 없으므로 message빈을 @Autowiried의 타입 자동와이어링으로 가져오지 않고 ApplicationContext를 이용했다.
        Object message = context.getBean( "message" );
        assertThat( message ).isInstanceOf( Message.class );
        assertThat( ( (Message) message ).getText() ).isEqualTo( "Factory Bean" );
    }

    @Test
    public void getFactoryBean() {
        // &를 붙여서 팩토리 빈 자체를 가져온다.
        Object factory = context.getBean( "&message" );
        assertThat( factory ).isInstanceOf( MessageFactoryBean.class );
    }
}
