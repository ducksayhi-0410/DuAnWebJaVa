package Db;

import Models.Account;
import Models.Cart;
import Models.Item;
import Models.Order; // <-- Thêm import
import Models.OrderDetail; // <-- Thêm import
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; // <-- Thêm import
import java.util.List; // <-- Thêm import

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
    
    // --- CÁC PHƯƠNG THỨC MỚI ĐỂ LẤY LỊCH SỬ/BIÊN LAI ---

    /**
     * Lấy tất cả đơn hàng của 1 user, sắp xếp mới nhất lên đầu.
     * (Dùng cho trang Lịch sử mua hàng)
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
                    
                    // Lấy các sản phẩm chi tiết cho đơn hàng này
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
     * Lấy thông tin chi tiết (sản phẩm) của 1 đơn hàng.
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
     * Lấy thông tin 1 đơn hàng VÀ kiểm tra xem nó có thuộc về user_hiện_tại không
     * (Dùng cho trang Xuất biên lai)
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
                    
                    // Lấy luôn các sản phẩm của đơn hàng này
                    order.setDetails(getOrderDetails(orderId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy đơn hàng theo ID: " + e.getMessage());
        }
        return order; // Sẽ là NULL nếu đơn hàng không tồn tại hoặc không phải của user
    }
}