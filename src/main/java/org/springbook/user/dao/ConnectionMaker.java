package org.springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 인터페이스 사용
 */
public interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
