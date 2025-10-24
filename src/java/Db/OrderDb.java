package Db;
import Models.Account;
import Models.Cart;
import Models.Item;
import java.sql.Statement;

import Models.Order;
import Models.OrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDb {

    /**
     * Lấy tất cả các đơn hàng của một người dùng, bao gồm cả chi tiết
     * của từng đơn hàng.
     */
    public List<Order> getOrdersByUsername(String username) {
        List<Order> orderList = new ArrayList<>();
        // Sắp xếp đơn hàng mới nhất lên đầu
        String sqlOrders = "SELECT * FROM orders WHERE username = ? ORDER BY order_date DESC";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement psOrders = conn.prepareStatement(sqlOrders)) {
            
            psOrders.setString(1, username);
            ResultSet rsOrders = psOrders.executeQuery();
            
            // Lặp qua từng đơn hàng
            while (rsOrders.next()) {
                Order order = new Order();
                order.setId(rsOrders.getInt("id"));
                order.setUsername(rsOrders.getString("username"));
                order.setOrderDate(rsOrders.getTimestamp("order_date")); // Lấy kiểu Timestamp
                order.setTotalMoney(rsOrders.getDouble("total_money"));
                order.setStatus(rsOrders.getString("status"));
                order.setShippingAddress(rsOrders.getString("shipping_address"));
                order.setShippingPhone(rsOrders.getString("shipping_phone"));
                
                // Với mỗi đơn hàng, lấy chi tiết của nó
                List<OrderDetail> details = getOrderDetails(conn, order.getId());
                order.setDetails(details);
                
                orderList.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
        return orderList;
    }

    /**
     * Hàm trợ giúp, lấy chi tiết đơn hàng theo order_id.
     * Dùng lại connection đã có để tối ưu.
     */
    private List<OrderDetail> getOrderDetails(Connection conn, int orderId) throws SQLException {
        List<OrderDetail> detailList = new ArrayList<>();
        String sqlDetails = "SELECT * FROM order_details WHERE order_id = ?";
        
        try (PreparedStatement psDetails = conn.prepareStatement(sqlDetails)) {
            psDetails.setInt(1, orderId);
            ResultSet rsDetails = psDetails.executeQuery();
            
            while (rsDetails.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setId(rsDetails.getInt("id"));
                detail.setOrderId(rsDetails.getInt("order_id"));
                detail.setProductId(rsDetails.getInt("product_id"));
                detail.setProductName(rsDetails.getString("product_name"));
                detail.setPrice(rsDetails.getDouble("price"));
                detail.setQuantity(rsDetails.getInt("quantity"));
                detailList.add(detail);
            }
        }
        return detailList;
    }
    
    // *** LƯU Ý QUAN TRỌNG ***
    // Bạn sẽ cần một phương thức createOrder() ở đây.
    // Phương thức này sẽ được gọi bởi "CheckoutServlet" (bạn chưa tạo)
    // để lưu giỏ hàng vào các bảng này khi người dùng thanh toán.

   /**
     * Tạo một đơn hàng mới từ giỏ hàng.
     * Đây là một giao dịch (transaction):
     * 1. Thêm vào bảng 'orders'.
     * 2. Thêm vào bảng 'order_details' (cho mỗi sản phẩm).
     * 3. Cập nhật (trừ) số lượng trong bảng 'products'.
     * Nếu 1 trong 3 bước lỗi, tất cả sẽ được rollback.
     * @return true nếu đặt hàng thành công, false nếu thất bại (ví dụ: hết hàng).
     */
    public boolean createOrder(Account acc, Cart cart, String shippingAddress, String shippingPhone) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rsKeys = null;

        String sqlOrder = "INSERT INTO orders (username, total_money, status, shipping_address, shipping_phone) VALUES (?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, product_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";

        try {
            conn = new DBContext().getConnection();
            // Bắt đầu transaction, tắt tự động commit
            conn.setAutoCommit(false);

            // --- 1. Thêm vào bảng 'orders' ---
            // Yêu cầu trả về khóa tự động tăng (order_id)
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, acc.getUsername());
            psOrder.setDouble(2, cart.getTotalMoney());
            psOrder.setString(3, "Đang xử lý"); // Trạng thái mặc định
            psOrder.setString(4, shippingAddress);
            psOrder.setString(5, shippingPhone);
            
            psOrder.executeUpdate();
            
            // Lấy ID của đơn hàng vừa tạo
            rsKeys = psOrder.getGeneratedKeys();
            int orderId = -1;
            if (rsKeys.next()) {
                orderId = rsKeys.getInt(1);
            }
            if (orderId == -1) {
                // Nếu không lấy được ID, rollback và thoát
                System.err.println("Không tạo được orderId");
                conn.rollback();
                return false;
            }

            // --- 2. Thêm vào 'order_details' VÀ Trừ kho 'products' ---
            psDetail = conn.prepareStatement(sqlDetail);
            psUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (Item item : cart.getItems()) {
                // 2a. Trừ kho
                // Câu SQL này sẽ chỉ thành công nếu số lượng tồn kho (quantity) >= số lượng mua
                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProduct().getId());
                psUpdateStock.setInt(3, item.getQuantity()); // Điều kiện `quantity >= ?`
                
                int rowsAffected = psUpdateStock.executeUpdate();
                
                // Nếu rowsAffected = 0, nghĩa là hàng không đủ (do quantity < item.getQuantity())
                if (rowsAffected == 0) {
                    System.err.println("Hết hàng cho sản phẩm ID: " + item.getProduct().getId());
                    conn.rollback(); // Hủy toàn bộ giao dịch
                    return false; // Báo lỗi hết hàng
                }

                // 2b. Thêm vào chi tiết đơn hàng
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getProduct().getId());
                psDetail.setString(3, item.getProduct().getName());
                psDetail.setDouble(4, item.getProduct().getPrice());
                psDetail.setInt(5, item.getQuantity());
                psDetail.addBatch(); // Thêm vào lô lệnh
            }
            
            // Thực thi hàng loạt các lệnh insert chi tiết
            psDetail.executeBatch();

            // --- 3. Hoàn tất giao dịch ---
            conn.commit(); // Lưu tất cả thay đổi vào CSDL
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo đơn hàng: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                }
            } catch (SQLException e2) {
                System.err.println("Lỗi khi rollback: " + e2.getMessage());
            }
            return false;
        } finally {
            // Đóng tất cả kết nối
            try { if (rsKeys != null) rsKeys.close(); } catch (Exception e) { /* ignored */ }
            try { if (psOrder != null) psOrder.close(); } catch (Exception e) { /* ignored */ }
            try { if (psDetail != null) psDetail.close(); } catch (Exception e) { /* ignored */ }
            try { if (psUpdateStock != null) psUpdateStock.close(); } catch (Exception e) { /* ignored */ }
            try { if (conn != null) conn.close(); } catch (Exception e) { /* ignored */ }
        }
    }
}