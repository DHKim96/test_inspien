package com.inspien.common.util;

import com.inspien.common.exception.DbCustomException;

import java.sql.*;

public class JDBCTemplate {
    public static Connection getConnection(String host, String port, String sid, String user, String password) {
        Connection conn = null;

        String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
        String className = "oracle.jdbc.driver.OracleDriver";
        try {
            // 1. JDBC Driver 등록
            Class.forName(className);

            // 2. Connection 객체 생성
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new DbCustomException(className + "을 찾을 수 없습니다.");
        } catch (SQLException e) {
            throw new DbCustomException("DriverManager 연결에 실패했습니다.");
        }

        return conn;
    }

    // 2. commit처리해주는 메소드(Connection객체 전달 받아서)
    public static void commit(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. rollback처리해주는 메소드(Connection객체 전달 받아서)
    public static void rollback(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //JDBC용 객체들을 전달받아서 반납처리해주는 메소드
    // 4. Statement관련 객체를 전달받아서 반납시켜주는 메소드
    public static void close(Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Statement관련 객체를 전달받아서 반납시켜주는 메소드
    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. ResulSet 객체를 전달받아서 반납시켜주는 메소드
    public static void close(ResultSet rset) {
        try {
            if (rset != null && !rset.isClosed()) {
                rset.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
