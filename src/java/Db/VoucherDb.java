package Db;

import Models.Voucher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Dùng Timestamp cho DateTime SQL
import java.util.ArrayList;
import java.util.Date; // Dùng Date cho Java
import java.util.List;

public class VoucherDb {

    /**
     * Lấy TẤT CẢ voucher (cho trang Admin)
     */
    public List<Voucher> getAllVouchers() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY expiry_date DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Voucher(
                    rs.getInt("id"), rs.getString("code"),
                    rs.getString("discount_type"), rs.getDouble("discount_value"),
                    rs.getDouble("min_order_value"), rs.getInt("max_usage"),
                    rs.getInt("current_usage"), rs.getTimestamp("expiry_date"),
                    rs.getString("created_by")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả voucher: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy MỘT voucher HỢP LỆ (cho Khách hàng)
     */
    public Voucher getValidVoucherByCode(String code) {
        String sql = "SELECT * FROM vouchers WHERE code = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Kiểm tra 1: Hạn sử dụng
                    Date expiryDate = rs.getTimestamp("expiry_date");
                    if (expiryDate.before(new Date())) {
                        return null; // Đã hết hạn
                    }
                    
                    // Kiểm tra 2: Lượt sử dụng
                    int currentUsage = rs.getInt("current_usage");
                    int maxUsage = rs.getInt("max_usage");
                    if (currentUsage >= maxUsage) {
                        return null; // Đã hết lượt
                    }
                    
                    // Hợp lệ -> trả về Voucher
                    return new Voucher(
                        rs.getInt("id"), rs.getString("code"),
                        rs.getString("discount_type"), rs.getDouble("discount_value"),
                        rs.getDouble("min_order_value"), maxUsage,
                        currentUsage, expiryDate,
                        rs.getString("created_by")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy voucher theo code: " + e.getMessage());
        }
        return null; // Không tìm thấy
    }

    /**
     * Thêm voucher mới (cho Admin)
     */
    public boolean addVoucher(String code, String type, double value, double minOrder, int maxUsage, Date expiryDate, String createdBy) {
        String sql = "INSERT INTO vouchers (code, discount_type, discount_value, min_order_value, max_usage, expiry_date, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            ps.setString(2, type);
            ps.setDouble(3, value);
            ps.setDouble(4, minOrder);
            ps.setInt(5, maxUsage);
            ps.setTimestamp(6, new Timestamp(expiryDate.getTime()));
            ps.setString(7, createdBy);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm voucher: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa voucher (cho Admin)
     */
    public boolean deleteVoucher(int id) {
        String sql = "DELETE FROM vouchers WHERE id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa voucher: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tăng lượt sử dụng (khi đặt hàng thành công)
     */
    public void incrementVoucherUsage(String code) {
        String sql = "UPDATE vouchers SET current_usage = current_usage + 1 WHERE code = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi tăng lượt dùng voucher: " + e.getMessage());
        }
    }
}