package Db;

import Models.Account;
import Models.OrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap; // Dùng map này để giữ thứ tự
import java.util.List;
import java.util.Map;

public class ReportDb {

    // (Giữ nguyên các hàm getTotalRevenue, getTopSellingProducts, getTopCustomers)
    
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_money) FROM orders WHERE status = 'Giao hàng thành công'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng doanh thu: " + e.getMessage());
        }
        return 0;
    }
    public List<OrderDetail> getTopSellingProducts() {
        List<OrderDetail> topProducts = new ArrayList<>();
        String sql = "SELECT d.product_id, d.product_name, SUM(d.quantity) AS total_quantity " +
                     "FROM order_details d JOIN orders o ON d.order_id = o.id " +
                     "WHERE o.status = 'Giao hàng thành công' " +
                     "GROUP BY d.product_id, d.product_name ORDER BY total_quantity DESC LIMIT 10";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderDetail item = new OrderDetail();
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("total_quantity"));
                topProducts.add(item);
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy sản phẩm bán chạy: " + e.getMessage()); }
        return topProducts;
    }
    public List<Account> getTopCustomers() {
        List<Account> topCustomers = new ArrayList<>();
        String sql = "SELECT o.username, a.fullname, SUM(o.total_money) AS total_spent " +
                     "FROM orders o JOIN accounts a ON o.username = a.username " +
                     "WHERE o.status = 'Giao hàng thành công' AND a.role = 'customer' " +
                     "GROUP BY o.username, a.fullname ORDER BY total_spent DESC LIMIT 5";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Account acc = new Account(
                    rs.getString("username"), null, null, 
                    rs.getString("fullname"), null, 
                    String.valueOf(rs.getDouble("total_spent")),
                    null
                );
                topCustomers.add(acc);
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy khách hàng mua nhiều: " + e.getMessage()); }
        return topCustomers;
    }

    // ==========================================================
    // === CÁC HÀM MỚI CHO BIỂU ĐỒ ===
    // ==========================================================

    /**
     * Lấy doanh thu hàng tháng (chỉ đơn thành công).
     * @return Map<Tháng-Năm, TổngDoanhThu>
     */
    public Map<String, Double> getMonthlyRevenueStats() {
        // Dùng LinkedHashMap để giữ thứ tự chèn (sắp xếp theo tháng)
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        
        // DATE_FORMAT(order_date, '%Y-%m') sẽ nhóm theo "2025-10", "2025-11"
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, SUM(total_money) AS total " +
                     "FROM orders " +
                     "WHERE status = 'Giao hàng thành công' " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY month ASC";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                monthlyData.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy doanh thu hàng tháng: " + e.getMessage());
        }
        return monthlyData;
    }

    /**
     * Lấy số lượng tài khoản đăng ký hàng tháng.
     * (Cần cột 'registration_date' trong bảng 'accounts')
     * @return Map<Tháng-Năm, SốLượngĐăngKý>
     */
    public Map<String, Double> getMonthlyRegistrationStats() {
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        
        // Tương tự, nhóm theo "Tháng-Năm"
        String sql = "SELECT DATE_FORMAT(registration_date, '%Y-%m') AS month, COUNT(*) AS total " +
                     "FROM accounts " +
                     "GROUP BY DATE_FORMAT(registration_date, '%Y-%m') " +
                     "ORDER BY month ASC";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                monthlyData.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            // Lỗi này sẽ xảy ra nếu bạn chưa chạy lệnh ALTER TABLE
            System.err.println("Lỗi khi lấy đăng ký hàng tháng (Kiểm tra cột 'registration_date'): " + e.getMessage());
        }
        return monthlyData;
    }
}