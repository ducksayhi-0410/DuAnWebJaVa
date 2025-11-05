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

    // (Giữ nguyên hàm createOrder)
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
            psOrder.setDouble(2, finalTotalMoney);
            psOrder.setString(3, address);
            psOrder.setString(4, phone);
            psOrder.executeUpdate();
            rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
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
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (psOrder != null) psOrder.close(); } catch (SQLException e) {}
             try { if (psDetail != null) psDetail.close(); } catch (SQLException e) {}
             try { if (psUpdateStock != null) psUpdateStock.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    // (Giữ nguyên các hàm getAllOrders, updateOrderStatus, getProcessingOrdersByUsername, getOrdersByUsername, getOrderDetails, getOrderByIdAndUser)
    // ...
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY CASE status WHEN 'Đang xác nhận' THEN 1 WHEN 'Đang chuẩn bị hàng' THEN 2 WHEN 'Đang giao hàng' THEN 3 WHEN 'Giao hàng thành công' THEN 4 ELSE 5 END, order_date DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUsername(rs.getString("username"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setStatus(rs.getString("status"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setShippingPhone(rs.getString("shipping_phone"));
                    order.setDetails(getOrderDetails(order.getId())); 
                    orderList.add(order);
                }
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy tất cả đơn hàng: " + e.getMessage()); }
        return orderList;
    }
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage()); return false; }
    }
    public List<Order> getProcessingOrdersByUsername(String username) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE username = ? AND (status = 'Đang xác nhận' OR status = 'Đang chuẩn bị hàng' OR status = 'Đang giao hàng') ORDER BY order_date DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setStatus(rs.getString("status"));
                    order.setDetails(getOrderDetails(order.getId()));
                    orderList.add(order);
                }
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy đơn hàng đang xử lý: " + e.getMessage()); }
        return orderList;
    }
    public List<Order> getOrdersByUsername(String username) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE username = ? ORDER BY order_date DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUsername(rs.getString("username"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setStatus(rs.getString("status"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setShippingPhone(rs.getString("shipping_phone"));
                    order.setDetails(getOrderDetails(order.getId()));
                    orderList.add(order);
                }
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy lịch sử đơn hàng: " + e.getMessage()); }
        return orderList;
    }
    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setId(rs.getInt("id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setProductName(rs.getString("product_name"));
                    detail.setPrice(rs.getDouble("price"));
                    detail.setQuantity(rs.getInt("quantity"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage()); }
        return details;
    }
    public Order getOrderByIdAndUser(int orderId, String username) {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE id = ? AND username = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUsername(rs.getString("username"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setStatus(rs.getString("status"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setShippingPhone(rs.getString("shipping_phone"));
                    order.setDetails(getOrderDetails(orderId));
                }
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy đơn hàng theo ID: " + e.getMessage()); }
        return order;
    }
    // ...

    // ==========================================================
    // === HÀM TRỢ GIÚP MỚI (ĐỂ LẤY TRẠNG THÁI CŨ) ===
    // ==========================================================
    
    /**
     * Lấy một đơn hàng bằng ID (dùng cho hệ thống).
     */
    public Order getOrderById(int orderId) {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUsername(rs.getString("username"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setStatus(rs.getString("status"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setShippingPhone(rs.getString("shipping_phone"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy đơn hàng theo ID: " + e.getMessage());
        }
        return order;
    }
}