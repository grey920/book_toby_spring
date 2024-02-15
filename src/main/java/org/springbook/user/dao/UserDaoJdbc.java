package org.springbook.user.dao;

import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDaoJdbc implements UserDao {

    /**
     * DataSource setter for JdbcTemplate
     * @param dataSource
     */
    public void setDataSource( DataSource dataSource ) {
        // Spring이 제공하는 JdbcTemplate 사용
        this.jdbcTemplate = new JdbcTemplate( dataSource );

    }

    private JdbcTemplate jdbcTemplate;

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow( ResultSet rs, int rowNum ) throws SQLException {
            User user = new User();
            user.setId( rs.getString( "id" ) );
            user.setName( rs.getString( "name" ) );
            user.setPassword( rs.getString( "password" ) );
            user.setEmail( rs.getString( "email" ) );
            user.setLevel( Level.valueOf( rs.getInt( "level" ) ) );
            user.setLogin( rs.getInt( "login" ) );
            user.setRecommend( rs.getInt( "recommend" ) );
            return user;
        }
    };

    public void add( User user ) {
        System.out.println( "UserDao.add() called" );

        this.jdbcTemplate.update( "insert into users(id, name, password, email, level, login, recommend) values (?, ?, ?, ?, ?, ?, ?)", user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }



    public User get( String id ){
        System.out.println( "UserDao.get() called" );
        return this.jdbcTemplate.queryForObject( "select * from users where id=?",
            new Object[]{ id }, // sql에 바인딩할 파라미터 값
            // 결과값을 오브젝트로 변환해줄 RowMapper
            this.userMapper );
    }

    public List<User> getAll(){
        return jdbcTemplate.query( "select * from users order by id", this.userMapper );
    }



    /**
     * 전략패턴의 클라이언트.
     * 사용할 전략 클래스 오브젝트를 생성하고, 컨텍스트 오브젝트에 주입해주는 역할 (DI 구조)
     * @throws SQLException
     */
    public void deleteAll(){
        System.out.println( "UserDao.deleteAll() called" );

        this.jdbcTemplate.update( "delete from users" );
    }



    public int getCount() {
        System.out.println( "UserDao.getCount() called" );

        return this.jdbcTemplate.queryForObject( "select count(*) from users", Integer.class );
    }

    @Override
    public void update( User user1 ) {
        this.jdbcTemplate.update(
            "update users set name=?, password=?, email=?, level=?, login=?, recommend=? where id=?",
            user1.getName(), user1.getPassword(), user1.getEmail(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getId() );
    }

}
