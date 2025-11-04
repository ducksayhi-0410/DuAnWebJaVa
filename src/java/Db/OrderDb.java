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
     * Đặt trạng thái mặc định là "Đang xác nhận".
     */
    public int createOrder(Account acc, Cart cart, String address, String phone) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;
        int orderId = -1;

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); 

            // === THAY ĐỔI TRẠNG THÁI MẶC ĐỊNH ===
            String sqlOrder = "INSERT INTO orders (username, total_money, shipping_address, shipping_phone, status) VALUES (?, ?, ?, ?, 'Đang xác nhận')";
            // === KẾT THÚC THAY ĐỔI ===
            
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, acc.getUsername());
            psOrder.setDouble(2, cart.getTotalMoney());
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
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            return -1;
        } finally {
            // (Đóng resources...)
        }
    }
    
    /**
     * 2. LẤY TẤT CẢ ĐƠN HÀNG (CHO ADMIN/NHÂN VIÊN) (ĐÃ CẬP NHẬT)
     * (Sắp xếp "Đang xác nhận" lên đầu)
     */
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY " +
                     "CASE status " +
                     "  WHEN 'Đang xác nhận' THEN 1 " +
                     "  WHEN 'Đang chuẩn bị hàng' THEN 2 " +
                     "  WHEN 'Đang giao hàng' THEN 3 " +
                     "  WHEN 'Giao hàng thành công' THEN 4 " +
                     "  ELSE 5 " +
                     "END, order_date DESC";
        
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả đơn hàng: " + e.getMessage());
        }
        return orderList;
    }

    /**
     * 3. CẬP NHẬT TRẠNG THÁI (CHO ADMIN/NHÂN VIÊN) (Giữ nguyên)
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 4. LẤY ĐƠN HÀNG ĐANG XỬ LÝ (CHO KHÁCH HÀNG) (ĐÃ CẬP NHẬT)
     * (Thêm "Đang xác nhận" vào logic)
     */
    public List<Order> getProcessingOrdersByUsername(String username) {
        List<Order> orderList = new ArrayList<>();
        // Chỉ lấy 3 trạng thái "Đang xác nhận", "Đang chuẩn bị hàng" VÀ "Đang giao hàng"
        String sql = "SELECT * FROM orders WHERE username = ? " +
                     "AND (status = 'Đang xác nhận' OR status = 'Đang chuẩn bị hàng' OR status = 'Đang giao hàng') " +
                     "ORDER BY order_date DESC";
        
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy đơn hàng đang xử lý: " + e.getMessage());
        }
        return orderList;
    }

    /**
     * 5. LẤY TẤT CẢ ĐƠN HÀNG (CHO "Lịch Sử Mua Hàng") (Giữ nguyên)
     */
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử đơn hàng: " + e.getMessage());
        }
        return orderList;
    }

    /**
     * 6. HÀM TRỢ GIÚP (Giữ nguyên)
     */
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        }
        return details;
    }
    
    /**
     * 7. HÀM TRỢ GIÚP (Giữ nguyên)
     */
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy đơn hàng theo ID: " + e.getMessage());
        }
        return order;
    }
}