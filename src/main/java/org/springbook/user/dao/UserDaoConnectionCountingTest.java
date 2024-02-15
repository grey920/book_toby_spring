package org.springbook.user.dao;

import org.springbook.CountingDaoFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main( String[] args ) throws SQLException, ClassNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext( CountingDaoFactory.class );
        UserDaoJdbc dao = context.getBean( "userDao", UserDaoJdbc.class );
        // 1
//        User newUser = new User();
//        newUser.setId( "dawa" );
//        newUser.setName( "다와" );
//        newUser.setPassword( "1234" );
//        dao.add( newUser );

        // 2
        dao.get( "dawa" );
        // 3
//        dao.get( "grey" );

        CountingConnectionMaker ccm = context.getBean( "connectionMaker", CountingConnectionMaker.class );
        System.out.println( "ccm.getCounter() = " + ccm.getCounter() );
    }
}
