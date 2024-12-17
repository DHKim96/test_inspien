package com.inspien.model.dao;

import com.inspien.model.dto.ItemResponse;
import com.inspien.model.dto.OrderInsert;
import com.inspien.model.dto.OrderResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDao {
    public int insertOrder(Connection conn, OrderResponse order, ItemResponse item) {
        int res = 0;

        String sql = """
                INSERT INTO INSPIEN_XMLDATA_INFO
                (ORDER_NUM, ORDER_ID, ORDER_DATE, ORDER_PRICE, ORDER_QTY, RECEIVER_NAME, RECEIVER_NO, ETA_DATE, DESTINATION, DESCIPTION,
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

            System.out.println(res);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
