package com.inspien.xml.dao;

import com.inspien.common.exception.DbCustomException;
import com.inspien.common.util.ErrCode;
import com.inspien.common.util.JDBCTemplate;
import com.inspien.xml.dto.ItemResponse;
import com.inspien.xml.dto.OrderResponse;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * XML_DATA 중 주문 데이터에 대한 DB 작업을 수행하는 DAO 클래스.
 */
@Slf4j
public class OrderDao {

    /**
     * INSPIEN_XMLDATA_INFO 테이블에 Order 및 Item 데이터를 삽입합니다.
     *
     * @param conn  데이터베이스 연결 객체
     * @param order OrderResponse 객체 (주문 헤더 정보)
     * @param item  ItemResponse 객체 (주문 상세 정보)
     * @return 삽입된 행 수 (1이면 성공, 0이면 실패)
     * @throws SQLException       SQL 실행 중 오류가 발생한 경우
     * @throws DbCustomException  자원 반납 중 오류가 발생한 경우
     */
    public int insertOrder(Connection conn, OrderResponse order, ItemResponse item) throws SQLException, DbCustomException {
        int res = 0;

        String sql = """
                INSERT INTO INSPIEN_XMLDATA_INFO
                (
                ORDER_NUM, ORDER_ID, ORDER_DATE, ORDER_PRICE, ORDER_QTY, RECEIVER_NAME, RECEIVER_NO, ETA_DATE, DESTINATION, DESCIPTION,
                ITEM_SEQ, ITEM_NAME, ITEM_QTY, ITEM_COLOR, ITEM_PRICE
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, order.getOrderNum());
        ps.setString(2, order.getOrderId());
        ps.setString(3, order.getOrderDate());
        ps.setInt(4, order.getOrderPrice());
        ps.setInt(5, order.getOrderQty());
        ps.setString(6, order.getReceiverName());
        ps.setString(7, order.getReceiverNo());
        ps.setString(8, order.getEtaDate());
        ps.setString(9, order.getDestination());
        ps.setString(10, order.getDescription());
        ps.setInt(11, item.getItemSeq());
        ps.setString(12, item.getItemName());
        ps.setInt(13, item.getItemQty());
        ps.setString(14, item.getItemColor());
        ps.setInt(15, item.getItemPrice());

        res = ps.executeUpdate();

        JDBCTemplate.close(ps);

        return res;
    }
}
