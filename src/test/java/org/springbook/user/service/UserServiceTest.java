package org.springbook.user.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springbook.user.dao.UserDao;
import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;
import org.springbook.user.service.UserServiceImpl.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static org.springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;


@SpringJUnitConfig(locations = "/test-applicationContext.xml") // JUnit4의 RunWith(SpringJUnit4ClassRunner.class) + ContextConfiguration(locations = "/applicationContext.xml")
class UserServiceTest {

    @Autowired UserDao userDao;
    @Autowired UserService userService;
    @Autowired UserServiceImpl userServiceImpl;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired UserLevelUpgradePolicy userLevelUpgradePolicy;
    @Autowired MailSender mailSender;

    // 테스트 픽스처
    List< User > users;

    @BeforeEach
    public void setUp(){
        users = Arrays.asList(
            new User("gyumee", "정겨운", "p1", "aaa@gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0 ),
            new User("leegw700", "정다와", "p2", "bbb@gmail.com",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
            new User("bumjin", "정중기", "p3", "ccc@gmail.com",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
            new User("leemj", "이미자", "p4", "ddd@gmail.com",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
            new User("green", "오민규", "p5", "eee@gmail.com",Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    /**
     * 트랜잭션 테스트
     */
    @Test
    public void upgradeAllOrNothing(){

        // TestUserService는 이 테스트에서만 쓸 것이기 때문에 굳이 빈으로 등록하지 않는다.
        TestUserService testUserService = new TestUserService( users.get( 3 ).getId() );
        // 수동DI
        testUserService.setUserDao( this.userDao );
        testUserService.setMailSender( this.mailSender );
        testUserService.setUserLevelUpgradePolicy( this.userLevelUpgradePolicy );

        // 트랜잭션 기능을 분리한 UserServiceTx를 생성하고 설정정보와 의존 오브젝트 주입
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager( this.transactionManager );
        userServiceTx.setUserService( testUserService );

        userDao.deleteAll();
        for ( User user : users ) {
            userDao.add( user );
        }

        try {
            userServiceTx.upgradeLevels(); // 트랜잭션 기능이 있는 UserServiceTx를 통해 testUserService가 호출되어야 함
            fail("TestUserServiceException expected");
        }
        // TestUserService가 주는 예외는 잡아서 계속 진행함.
        catch ( UserServiceImpl.TestUserServiceException e ) {
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        // 예외가 발생하기 전에 업그레이드가 된 사용자는 레벨이 그대로 유지됐어야 함
        checkLevelUpgraded( users.get( 1 ), false );
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get( 4 ); // GOLD 레벨
        User userWithoutLevel = users.get( 0 );
        userWithoutLevel.setLevel( null ); // 레벨이 비어있는 사용자. 로직에 따라 등록 중에 BASIC 레벨로 설정돼야 함

        userService.add( userWithLevel );
        userService.add( userWithoutLevel );

        // DB에 저장된 결과를 꺼내 확인
        User userWithLevelRead = userDao.get( userWithLevel.getId() );
        User userWithoutLevelRead = userDao.get( userWithoutLevel.getId() );

        assertEquals( userWithLevel.getLevel(), userWithLevelRead.getLevel() );
        assertEquals( Level.BASIC, userWithoutLevelRead.getLevel() );
    }

    @Test
    @DirtiesContext // 컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려줌
    public void upgradeLevels() throws Exception {
        // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 됨
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserLevelUpgradePolicy( new UserLevelUpgradePolicyImpl() );

        // 목 오브젝트로 만든 UserDao를 직접 DI
        MockUserDao mockUserDao = new MockUserDao( this.users );
        userServiceImpl.setUserDao( mockUserDao );

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender( mockMailSender );

        userServiceImpl.upgradeLevels();

        // MockUserDao로부터 업데이트 결과 가져옴
        List< User > updated = mockUserDao.getUpdated();
        // 업데이트 횟수와 정보 확인
        assertThat( updated.size(), is( 2 ) );
        checkUserAndLevel( updated.get( 0 ), "leegw700", Level.SILVER );
        checkUserAndLevel( updated.get( 1 ), "leemj", Level.GOLD );

        /* [Mock] 목 오브젝트를 이용한 결과 확인 */
        List< String > requests = mockMailSender.getRequests();
        MatcherAssert.assertThat( requests.size(), is( 2 ) );
        assertThat( requests.get( 0 ), is( users.get( 1 ).getEmail() ) );
        assertThat( requests.get( 1 ), is( users.get( 3 ).getEmail() ) );

    }

    /**
     * id와 level을 확인하는 간단한 헬퍼 메소드
     * @param user
     * @param expectedId
     * @param level
     */
    private void checkUserAndLevel( User updated, String expectedId, Level expectedLevel ) {
        assertThat( updated.getId(), is( expectedId ) );
        assertThat( updated.getLevel(), is( expectedLevel ) );
    }

    private void checkLevelUpgraded( User user, boolean upgraded ) {
        User userUpdate = userDao.get( user.getId() );
        if ( upgraded ) {
            assertEquals( user.getLevel().nextLevel(), userUpdate.getLevel() );
        }
        else {
            assertEquals( user.getLevel(), userUpdate.getLevel() );
        }
    }

    private void checkLevel( User user, Level expectedLevel ) {
        User userUpdate = userDao.get( user.getId() );
        assertEquals( expectedLevel, userUpdate.getLevel() );
    }

    /**
     * UserServiceImpl의 upgradeLevels() 메소드에서 사용하는 userDao의 기능은 getAll()과 update()이다.
     * - getAll()은 레벨 업그레이드 후보 목록만 제공하면 된다. 스텁으로 동작하면 됨
     * - update()는 업그레이드로 레벨이 '변경'됨을 확인해야 하므로 Mock Object로서 동작해야 함
     */
    static class MockUserDao implements UserDao {
        private List<User> users; // 레벨 업그레이드 후보 User 오브젝트 목록. getAll()의 리턴
        private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

        private MockUserDao( List<User> users ){
            this.users = users;
        }

        public List<User> getUpdated(){
            return this.updated;
        }

        /**
         * 스텁 기능 제공
         * @return
         */
        @Override
        public List< User > getAll() {
            return this.users;
        }

        /**
         * 목 오브젝트 기능 제공
         * @param user1
         */
        @Override
        public void update( User user ) {
            updated.add( user );
        }

        // ----------------> 테스트에 사용되지 않는 메소드
        @Override
        public void add( User user ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get( String id ) {
            throw new UnsupportedOperationException();
        }


        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }

    }


    /**
     * 메일 전송 확인용 클래스
     * 전송 요청을 받은 메일 주소를 저장해주고 이를 읽을 수 있게 한다.
     */
    static class MockMailSender implements MailSender {
        private List< String > requests = new ArrayList<>();
        public List< String > getRequests() {
            return requests;
        }

        @Override
        public void send( SimpleMailMessage simpleMessage ) throws MailException {
            requests.add( simpleMessage.getTo()[0] ); // 첫 번째 수신자 메일 주소만 저장
        }
        @Override
        public void send( SimpleMailMessage... simpleMessages ) throws MailException {
        }
    }
}