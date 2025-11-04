package Db;

import Models.Account;
import Models.Cart;
import Models.Item;
import Models.Order;
import Models.OrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDb {

    /**
     * 1. TẠO ĐƠN HÀNG (ĐÃ CẬP NHẬT)
     * - Nhận 'finalTotalMoney' thay vì tính toán từ 'cart'
     * - Đặt trạng thái mặc định là "Đang xác nhận".
     */
    public int createOrder(Account acc, Cart cart, String address, String phone, double finalTotalMoney) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;
        int orderId = -1;

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); 

            String sqlOrder = "INSERT INTO orders (username, total_money, shipping_address, shipping_phone, status) VALUES (?, ?, ?, ?, 'Đang xác nhận')";
            
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, acc.getUsername());
            
            // === THAY ĐỔI QUAN TRỌNG: Dùng tổng tiền đã giảm ===
            psOrder.setDouble(2, finalTotalMoney);
            // ===========================================
            
            psOrder.setString(3, address);
            psOrder.setString(4, phone);
            psOrder.executeUpdate();
            
            rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // (Phần logic thêm 'order_details' và 'update products' giữ nguyên)
            String sqlDetail = "INSERT INTO order_details (order_id, product_id, product_name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            psDetail = conn.prepareStatement(sqlDetail);
            psUpdateStock = conn.prepareStatement(sqlStock);
            for (Item item : cart.getItems()) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getProduct().getId());
                psDetail.setString(3, item.getProduct().getName());
                psDetail.setInt(4, item.getQuantity());
                psDetail.setDouble(5, item.getProduct().getPrice());
                psDetail.addBatch();
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProduct().getId());
                psUpdateStock.addBatch();
            }
            psDetail.executeBatch();
            psUpdateStock.executeBatch();
            
            conn.commit(); 
            return orderId;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo đơn hàng: " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) {} }
            return -1;
        } finally {
            // (Đóng resources...)
        }
    }
    
    // (Tất cả các hàm khác: getAllOrders, updateOrderStatus, getProcessingOrdersByUsername, getOrdersByUsername, getOrderDetails, getOrderByIdAndUser... GIỮ NGUYÊN)
    // ...
    public List<Order> getAllOrders() { /* (Giữ nguyên code) */ 
        return new ArrayList<>();
    }
    public boolean updateOrderStatus(int orderId, String newStatus) { /* (Giữ nguyên code) */ 
        return false;
    }
    public List<Order> getProcessingOrdersByUsername(String username) { /* (Giữ nguyên code) */ 
        return new ArrayList<>();
    }
    public List<Order> getOrdersByUsername(String username) { /* (Giữ nguyên code) */ 
        return new ArrayList<>();
    }
    public List<OrderDetail> getOrderDetails(int orderId) { /* (Giữ nguyên code) */ 
        return new ArrayList<>();
    }
    public Order getOrderByIdAndUser(int orderId, String username) { /* (Giữ nguyên code) */ 
        return null;
    }
    // ...
}