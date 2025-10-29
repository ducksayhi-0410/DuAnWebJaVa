package Db;

import Models.Account;
import Models.Cart;
import Models.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDb {

    public int createOrder(Account acc, Cart cart, String address, String phone) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;
        int orderId = -1;

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction
            
            // 1. Thêm vào bảng 'orders' (Dùng cột "username")
            String sqlOrder = "INSERT INTO orders (username, total_money, shipping_address, shipping_phone, status) VALUES (?, ?, ?, ?, 'Pending')";
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, acc.getUsername());
            psOrder.setDouble(2, cart.getTotalMoney());
            psOrder.setString(3, address);
            psOrder.setString(4, phone);
            psOrder.executeUpdate();
            
            // 2. Lấy ID đơn hàng
            rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // 3. Thêm vào 'order_details' (Dùng cột "product_name")
            String sqlDetail = "INSERT INTO order_details (order_id, product_id, product_name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            
            psDetail = conn.prepareStatement(sqlDetail);
            psUpdateStock = conn.prepareStatement(sqlStock);

            for (Item item : cart.getItems()) {
                // Thêm chi tiết
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getProduct().getId());
                psDetail.setString(3, item.getProduct().getName()); // Khớp với CSDL
                psDetail.setInt(4, item.getQuantity());
                psDetail.setDouble(5, item.getProduct().getPrice());
                psDetail.addBatch();
                
                // Trừ kho
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProduct().getId());
                psUpdateStock.addBatch();
            }
            
            psDetail.executeBatch();
            psUpdateStock.executeBatch();
            
            conn.commit(); // Hoàn tất
            return orderId;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo đơn hàng: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Hủy bỏ nếu lỗi
                } catch (SQLException ex) {}
            }
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psDetail != null) psDetail.close();
                if (psUpdateStock != null) psUpdateStock.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {}
        }
    }
}