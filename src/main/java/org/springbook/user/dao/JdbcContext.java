package org.springbook.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {

    private DataSource dataSource;

    /**
     * DataSource를 DI 받도록 준비한다. (인스턴스 변수, setter 메소드)
     * @param dataSource
     */
    public void setDataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    public void executeSql( final String query ) throws SQLException {
        workWithStatementStrategy(
            new StatementStrategy() {
                @Override
                public PreparedStatement makePreparedStatement( Connection c ) throws SQLException {
                    return c.prepareStatement( query );
                }
            }
        );

    }

    public void workWithStatementStrategy( StatementStrategy stmt ) throws SQLException {
        // 커넥션을 가져온다.
        // 전략 오브젝트의 메소드를 호출하면서 커넥션을 전달해준다.
        try( Connection c = dataSource.getConnection();  PreparedStatement ps = stmt.makePreparedStatement( c ) ) {
            ps.executeUpdate();
        }
    }


}
