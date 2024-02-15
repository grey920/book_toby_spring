package org.springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker{

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        Connection c = DriverManager.getConnection( "jdbc:mysql://localhost:3306/toby_spring", "root", "nea8041" );
        return c;
    }
}
