package org.springbook;

import org.springbook.user.dao.UserDaoJdbc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main( String[] args ) {

        DaoFactory factory = new DaoFactory();
        UserDaoJdbc dao1 = factory.userDao();
        UserDaoJdbc dao2 = factory.userDao();

        // 다른 값 출력
        System.out.println( dao1 );
        System.out.println( dao2 );
        System.out.println( "dao1 == dao2 " + (dao1 == dao2) );

        /*스프링 -> 기본 싱글톤 객체 리턴*/
        ApplicationContext context = new AnnotationConfigApplicationContext( DaoFactory.class );
        UserDaoJdbc dao3 = context.getBean( "userDao", UserDaoJdbc.class );
        UserDaoJdbc dao4 = context.getBean( "userDao", UserDaoJdbc.class );

        System.out.println( "dao3 = " + dao3 );
        System.out.println( "dao4 = " + dao4 );
        System.out.println( "dao3 == dao4 " + (dao4 == dao4) );
    }
}