package org.springbook.user.dao;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장 기능 지정 (spring-context, spring-test dependency 필요)
@SpringJUnitConfig(locations = "/test-applicationContext.xml") // JUnit4의 RunWith(SpringJUnit4ClassRunner.class) + ContextConfiguration(locations = "/applicationContext.xml")
class UserDaoTest {
    @Autowired
    private UserDao dao;
    @Autowired
    DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    /**
     * static으로 선언 후 @BeforeAll로 돌린게 더 빠름
     */
    @BeforeEach
    public void setUp() {

        this.user1 = new User("gyumee", "정겨운", "1234", "aaa@gmail.com", Level.BASIC, 1, 0 );
        this.user2 = new User("leegw700", "정다와", "1234", "bbb@gmail.com",Level.SILVER, 55, 10);
        this.user3 = new User("bumjin", "정중기", "1234", "ccc@gmail.com", Level.GOLD, 100, 40);
    }

    @Test
    public void sqlExceptionTranslate() {
        dao.deleteAll();
        assertEquals( 0, dao.getCount() );

        try {
            dao.add( user1 );
            dao.add( user1 );
        }
        catch ( DuplicateKeyException e ) {
            SQLException sqlException = ( SQLException ) e.getRootCause(); // getRootCause() : 최초 발생한 SQLException을 가져옴

            // 코드를 이용한 SQLException 전환
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator( this.dataSource ); // dataSource를 이용해 SQLExceptionTranslator의 translate()로 SQLException을 DataAccessException 타입 예외로 변환
            Assertions.assertThrows( DuplicateKeyException.class, () -> {
                throw set.translate( null, null, sqlException );
            } );
        }
    }

    /**
     * JDBC나 JPA 등 같은 상황이어도 구현체마다 에러가 다르다.
     * 이를 동일하게 처리하려면
     * 1) DuplicateKeyException의 상위인 DataIntegrityViolationException 으로 처리
     * 2) DuplicatedUserIdException 등의 커스텀 예외를 만들어서 구현체마다 다르게 처리
     */
    @Test
    public void duplicateKey() {
        dao.deleteAll();
        assertEquals( 0, dao.getCount() );

        // 키 값 중복시 JDBC는 DuplicateKeyException 에러이지만 JPA나 하이버네이트는 다른 에러를 뱉는다.
        Assertions.assertThrows( DuplicateKeyException.class, () -> {
            dao.add( user1 );
            dao.add( user1 );
        } );
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertEquals( 0, dao.getCount() );

        dao.add( user1 );
        dao.add( user2 );
        dao.add( user3 );
        assertEquals( 3, dao.getCount() );

        User userget1 = dao.get( user1.getId() );
        checkSameUser( user1, userget1 );

        User userget2 = dao.get( user2.getId() );
        checkSameUser( user2, userget2 );

    }

    @Test
    public void update(){
        dao.deleteAll();

        dao.add( user1 );
        dao.add( user2 );

        user1.setName( "이미자" );
        user1.setPassword( "9876" );
        user1.setLevel( Level.GOLD );
        user1.setLogin( 1000 );
        user1.setRecommend( 999 );
        dao.update( user1 );

        User user1update = dao.get( user1.getId() );
        checkSameUser( user1, user1update );

        User user2same = dao.get( user2.getId() );
        checkSameUser( user2, user2same );
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        assertEquals( 0, dao.getCount() );

        dao.add( user1 );
        assertEquals( 1, dao.getCount() );

        dao.add( user2 );
        assertEquals( 2, dao.getCount() );

        dao.add( user3 );
        assertEquals( 3, dao.getCount() );
    }

    /**
     * 존재하지 않는 id로 조회 시도 시 예외 발생
     * @throws SQLException
     */
    @Test
    public void getUserFailure() throws SQLException {

        dao.deleteAll();
        assertEquals( 0, dao.getCount() );

        Assertions.assertThrows( EmptyResultDataAccessException.class, () -> {
            dao.get( "unknown_id" );
        } );
    }

    @Test
    public void getAll() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertEquals( 0, dao.getCount() );

        List< User > users0 = dao.getAll();
        assertEquals( 0, users0.size() );

        dao.add( user1 );
        List<User> users1 = dao.getAll();
        assertEquals( 1, users1.size() );
        checkSameUser( user1, users1.get( 0 ) );

        dao.add( user2 );
        List<User> users2 = dao.getAll();
        assertEquals( 2, users2.size() );
        checkSameUser( user1, users2.get( 0 ) );
        checkSameUser( user2, users2.get( 1 ) );

        dao.add( user3 );
        List<User> users3 = dao.getAll();
        assertEquals( 3, users3.size() );
        checkSameUser( user3, users3.get( 0 ) );
        checkSameUser( user1, users3.get( 1 ) );
        checkSameUser( user2, users3.get( 2 ) );
    }

    /**
     * User 오브젝트 내용 비교 (동등성 비교)
     *
     * @param user1
     * @param user2
     */
    private void checkSameUser( User user1, User user2 ) {
        assertEquals( user1.getId(), user2.getId() );
        assertEquals( user1.getName(), user2.getName() );
        assertEquals( user1.getPassword(), user2.getPassword() );
        assertEquals( user1.getLevel(), user2.getLevel() );
        assertEquals( user1.getLogin(), user2.getLogin() );
        assertEquals( user1.getRecommend(), user2.getRecommend() );
    }


}