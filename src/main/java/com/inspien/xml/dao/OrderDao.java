package com.inspien.xml.dao;

import com.inspien.common.exception.DbCustomException;
import com.inspien.xml.dto.ItemResponse;
import com.inspien.xml.dto.OrderResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDao {
    public int insertOrder(Connection conn, OrderResponse order, ItemResponse item) {
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

        try(PreparedStatement ps = conn.prepareStatement(sql)) {

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

        } catch (SQLException e) {
            throw new DbCustomException("INSPIEN_XMLDATA_INFO 테이블 주문 데이터 INSERT SQL 오류가 발생했습니다. 에러 메시지 : " + e.getMessage(),  e);
        }

        return res;
    }
}
