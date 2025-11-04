package Db;

import Models.OrderDetail; 
import Models.Account; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDb {

    /**
     * Lấy Tổng doanh thu (CHỈ TÍNH ĐƠN "Giao hàng thành công")
     */
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

    /**
     * Lấy Top Sản phẩm bán chạy (CHỈ TÍNH ĐƠN "Giao hàng thành công")
     */
    public List<OrderDetail> getTopSellingProducts() {
        List<OrderDetail> topProducts = new ArrayList<>();
        String sql = "SELECT d.product_id, d.product_name, SUM(d.quantity) AS total_quantity " +
                     "FROM order_details d " +
                     "JOIN orders o ON d.order_id = o.id " +
                     "WHERE o.status = 'Giao hàng thành công' " +
                     "GROUP BY d.product_id, d.product_name " +
                     "ORDER BY total_quantity DESC " +
                     "LIMIT 10";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderDetail item = new OrderDetail();
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("total_quantity")); // Dùng tạm trường quantity
                topProducts.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy sản phẩm bán chạy: " + e.getMessage());
        }
        return topProducts;
    }

    /**
     * Lấy Top Khách hàng (CHỈ TÍNH ĐƠN "Giao hàng thành công")
     */
    public List<Account> getTopCustomers() {
        List<Account> topCustomers = new ArrayList<>();
        String sql = "SELECT o.username, a.fullname, SUM(o.total_money) AS total_spent " +
                     "FROM orders o " +
                     "JOIN accounts a ON o.username = a.username " +
                     "WHERE o.status = 'Giao hàng thành công' AND a.role = 'customer' " +
                     "GROUP BY o.username, a.fullname " +
                     "ORDER BY total_spent DESC " +
                     "LIMIT 5";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Account acc = new Account(
                    rs.getString("username"), null, null, 
                    rs.getString("fullname"), null, 
                    String.valueOf(rs.getDouble("total_spent")), // Dùng tạm Address để lưu tổng chi
                    null
                );
                topCustomers.add(acc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy khách hàng mua nhiều: " + e.getMessage());
        }
        return topCustomers;
    }
}