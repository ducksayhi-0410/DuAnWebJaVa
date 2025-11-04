package Db;

import Models.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDb {

    // === CẬP NHẬT HÀM checkLogin ===
    public Account checkLogin(String loginInput, String password) {
        // Lấy tất cả các cột
        String sql = "SELECT * FROM accounts WHERE (username = ? OR email = ?) AND password = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, loginInput); 
            ps.setString(2, loginInput); 
            ps.setString(3, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Gọi constructor 9 tham số mới
                    return new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("fullname"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("customer_tier"), // Thêm
                        rs.getDouble("lifetime_spend")  // Thêm
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra đăng nhập: " + e.getMessage());
        }
        return null;
    }
    
    // (Giữ nguyên các hàm checkUsernameExists, checkEmailExists, checkPhoneExists, createAccount)
    // ...
    public boolean checkUsernameExists(String username) {
        // (Giữ nguyên code)
        return false;
    }
    public boolean checkEmailExists(String email) {
        // (Giữ nguyên code)
        return false;
    }
    public boolean checkPhoneExists(String phone) {
        // (Giữ nguyên code)
        return false;
    }
    public boolean createAccount(String username, String email, String password, String fullname, String phone) {
        // (Giữ nguyên code - Lưu ý: hàm này sẽ dùng giá trị DEFAULT cho 2 cột mới)
        return false;
    }
    // ...

    // === CẬP NHẬT CÁC HÀM GET (NẾU BẠN CÓ) ===
    
    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("fullname"),
                        rs.getString("phone"), rs.getString("address"),
                        rs.getString("email"), rs.getString("customer_tier"),
                        rs.getDouble("lifetime_spend")
                    );
                }
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy tài khoản theo username: " + e.getMessage()); }
        return null;
    }
    
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY CASE role WHEN 'admin' THEN 1 WHEN 'nhanvien' THEN 2 ELSE 3 END, username";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accountList.add(new Account(
                    rs.getString("username"), rs.getString("password"),
                    rs.getString("role"), rs.getString("fullname"),
                    rs.getString("phone"), rs.getString("address"),
                    rs.getString("email"), rs.getString("customer_tier"),
                    rs.getDouble("lifetime_spend")
                ));
            }
        } catch (SQLException e) { System.err.println("Lỗi khi lấy tất cả tài khoản: " + e.getMessage()); }
        return accountList;
    }

    // (Giữ nguyên các hàm updateAccount, deleteAccount, changePassword, updateProfile...)
    // ...
    public boolean updateProfile(String username, String fullname, String phone, String address) {
        // (Giữ nguyên code)
        return false;
    }
    public boolean changePassword(String username, String newPass) {
        // (Giữ nguyên code)
        return false;
    }
    public boolean addAccount(Account acc) {
         // (Giữ nguyên code)
        return false;
    }
     public boolean updateAccount(Account acc, boolean updatePassword) {
         // (Giữ nguyên code)
        return false;
     }
      public boolean deleteAccount(String username) {
         // (Giữ nguyên code)
        return false;
      }
    // ...

    // ==========================================================
    // === HÀM MỚI QUAN TRỌNG: CẬP NHẬT CHI TIÊU VÀ HẠNG ===
    // ==========================================================
    public void updateCustomerSpendAndTier(String username, double newOrderTotal) {
        // Ngưỡng xếp hạng (Bạn có thể thay đổi)
        final double KIM_CUONG_TIER = 50000000; // 50 triệu
        final double VANG_TIER = 20000000;     // 20 triệu
        final double BAC_TIER = 5000000;       // 5 triệu

        Connection conn = null;
        PreparedStatement psGet = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        String sqlGet = "SELECT lifetime_spend, customer_tier FROM accounts WHERE username = ?";
        String sqlUpdate = "UPDATE accounts SET lifetime_spend = ?, customer_tier = ? WHERE username = ?";

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch
            
            // 1. Lấy tổng chi tiêu hiện tại
            psGet = conn.prepareStatement(sqlGet);
            psGet.setString(1, username);
            rs = psGet.executeQuery();

            if (rs.next()) {
                double currentSpend = rs.getDouble("lifetime_spend");
                
                // 2. Tính toán tổng mới
                double newTotalSpend = currentSpend + newOrderTotal;
                String newTier = "dong";

                // 3. Xác định hạng mới
                if (newTotalSpend >= KIM_CUONG_TIER) {
                    newTier = "kimcuong";
                } else if (newTotalSpend >= VANG_TIER) {
                    newTier = "vang";
                } else if (newTotalSpend >= BAC_TIER) {
                    newTier = "bac";
                }
                
                // 4. Cập nhật CSDL
                psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setDouble(1, newTotalSpend);
                psUpdate.setString(2, newTier);
                psUpdate.setString(3, username);
                psUpdate.executeUpdate();
                
                conn.commit(); // Hoàn tất giao dịch
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chi tiêu & hạng thành viên: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {}
        } finally {
            // Đóng resources
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (psGet != null) psGet.close(); } catch (SQLException e) {}
            try { if (psUpdate != null) psUpdate.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}