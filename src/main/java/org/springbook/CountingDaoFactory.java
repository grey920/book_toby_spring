package org.springbook;

import org.springbook.user.dao.ConnectionMaker;
import org.springbook.user.dao.CountingConnectionMaker;
import org.springbook.user.dao.DConnectionMaker;
import org.springbook.user.dao.UserDaoJdbc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class CountingDaoFactory {

    /*@Bean
    public UserDao userDao() {
        return new UserDao( connectionMaker() );
    }*/

    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setDataSource( dataSource() );
        return userDao;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass( com.mysql.cj.jdbc.Driver.class );
        dataSource.setUrl( "jdbc:mysql://localhost:3306/toby_spring" );
        dataSource.setUsername( "root" );
        dataSource.setPassword( "nea8041" );

        return dataSource;
    }


    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker( realConnectionMaker() );
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new DConnectionMaker();
    }
}
