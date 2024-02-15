package org.springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 독립시킨 DB 연결 기능
 */
public class SimpleConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        Connection c = DriverManager.getConnection( "jdbc:mysql://localhost:3306/toby_spring", "root", "nea8041" );
        return c;
    }
}
