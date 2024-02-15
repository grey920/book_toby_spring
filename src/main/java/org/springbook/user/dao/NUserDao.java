package org.springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 상속을 이용한 확장 방식
 */
//public class NUserDao extends UserDao{
//    /**
//     * DB 커넥션 중복 코드 메소드 추출
//     * -> 서브클래스가 각자 환경에 맞게 구현
//     *
//     * @return
//     * @throws ClassNotFoundException
//     * @throws SQLException
//     */
////    @Override
//    public Connection getConnection() throws ClassNotFoundException, SQLException {
//        Class.forName( "com.mysql.jdbc.Driver" );
//        Connection c = DriverManager.getConnection( "jdbc:mysql://localhost:3306/toby_spring", "root", "nea8041" );
//        return c;
//    }
//}
