package com.inspien.common.util;

import com.inspien.common.exception.DbCustomException;

import java.sql.*;

/**
 * JDBC 작업을 간소화하고 관리하기 위한 유틸리티 클래스.
 * <p>
 * 주요 역할:
 * <ul>
 *     <li>데이터베이스 연결(Connection) 생성</li>
 *     <li>트랜잭션 처리(commit, rollback)</li>
 *     <li>JDBC 리소스(Statement, ResultSet, Connection) 정리</li>
 * </ul>
 */
public class JDBCTemplate {


    /**
     * 데이터베이스 연결을 생성하는 메서드.
     *
     * @param host     데이터베이스 호스트 주소
     * @param port     데이터베이스 포트
     * @param sid      데이터베이스 SID
     * @param user     데이터베이스 사용자 이름
     * @param password 데이터베이스 사용자 비밀번호
     * @return 데이터베이스 연결 객체(Connection)
     * @throws DbCustomException 클래스 로드 실패 또는 연결 실패 시 발생
     */
    public static Connection getConnection(String host, String port, String sid, String user, String password) throws DbCustomException {
        Connection conn = null;

        String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
        String className = "oracle.jdbc.driver.OracleDriver";

        try {
            // 1. JDBC Driver 등록
            Class.forName(className);
            // 2. Connection 객체 생성
            conn = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            throw new DbCustomException(ErrCode.CLASS_NOT_FOUND,  className, e);
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.CONNECTION_FAILED,  "DriverManager", e);
        }

        return conn;
    }

    /**
     * 트랜잭션을 커밋하는 메서드.
     *
     * @param conn 데이터베이스 연결 객체
     * @throws DbCustomException 커밋 실패 시 발생
     */
    public static void commit(Connection conn) throws DbCustomException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
            }
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.DATABASE_COMMIT_FAILED, e);
        }
    }


    /**
     * 트랜잭션을 롤백하는 메서드.
     *
     * @param conn 데이터베이스 연결 객체
     * @throws DbCustomException 롤백 실패 시 발생
     */
    public static void rollback(Connection conn) throws DbCustomException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.DATABASE_ROLLBACK_FAILED, e);
        }
    }

    /**
     * Statement 객체를 닫는 메서드.
     *
     * @param stmt Statement 객체
     * @throws DbCustomException Statement 닫기 실패 시 발생
     */
    public static void close(Statement stmt) throws DbCustomException {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.CONNECTION_DISCONNECT_FAILED, "Statement", e);
        }
    }

    /**
     * ResultSet 객체를 닫는 메서드.
     *
     * @param rset ResultSet 객체
     * @throws DbCustomException ResultSet 닫기 실패 시 발생
     */
    public static void close(ResultSet rset) throws DbCustomException {
        try {
            if (rset != null && !rset.isClosed()) {
                rset.close();
            }
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.CONNECTION_DISCONNECT_FAILED, "ResultSet", e);
        }

    }

    /**
     * Connection 객체를 닫는 메서드.
     *
     * @param conn Connection 객체
     * @throws DbCustomException Connection 닫기 실패 시 발생
     */
    public static void close(Connection conn) throws DbCustomException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.CONNECTION_DISCONNECT_FAILED, "Connection", e);
        }
    }

}
