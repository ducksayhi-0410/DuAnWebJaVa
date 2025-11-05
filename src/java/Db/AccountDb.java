package Db;

import Models.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDb {

    // === HÀM checkLogin (ĐÃ ĐẦY ĐỦ) ===
    public Account checkLogin(String loginInput, String password) {
        String sql = "SELECT * FROM accounts WHERE (username = ? OR email = ?) AND password = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginInput); 
            ps.setString(2, loginInput); 
            ps.setString(3, password);
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
        } catch (SQLException e) { System.err.println("Lỗi khi kiểm tra đăng nhập: " + e.getMessage()); }
        return null;
    }

    // ==========================================================
    // === CÁC HÀM BỊ LỖI (ĐÃ ĐƯỢC VIẾT LẠI) ===
    // ==========================================================

    /**
     * KIỂM TRA USERNAME TỒN TẠI
     */
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT 1 FROM accounts WHERE username = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true (tồn tại) nếu tìm thấy
            }
        } catch (SQLException e) {
            System.err.println("Lỗi checkUsernameExists: " + e.getMessage());
            return true; // An toàn, giả sử là có lỗi
        }
    }

    /**
     * KIỂM TRA EMAIL TỒN TẠI
     */
    public boolean checkEmailExists(String email) {
        String sql = "SELECT 1 FROM accounts WHERE email = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true (tồn tại) nếu tìm thấy
            }
        } catch (SQLException e) {
            System.err.println("Lỗi checkEmailExists: " + e.getMessage());
            return true; // An toàn, giả sử là có lỗi
        }
    }

    /**
     * KIỂM TRA SĐT TỒN TẠI
     */
    public boolean checkPhoneExists(String phone) {
        String sql = "SELECT 1 FROM accounts WHERE phone = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true (tồn tại) nếu tìm thấy
            }
        } catch (SQLException e) {
            System.err.println("Lỗi checkPhoneExists: " + e.getMessage());
            return true; // An toàn, giả sử là có lỗi
        }
    }

    /**
     * TẠO TÀI KHOẢN MỚI
     * (Tự động gán vai trò 'customer')
     */
    public boolean createAccount(String username, String email, String password, String fullname, String phone) {
        
        // CSDL của bạn có DEFAULT role là 'nhanvien'
        // Chúng ta phải ghi đè nó thành 'customer' khi đăng ký
        
        String sql = "INSERT INTO accounts (username, email, password, fullname, phone, role) " +
                     "VALUES (?, ?, ?, ?, ?, 'customer')";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, fullname);
            ps.setString(5, phone);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu thành công

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo tài khoản: " + e.getMessage());
            return false;
        }
    }

    // ==========================================================
    // === CÁC HÀM CÒN LẠI (GIỮ NGUYÊN) ===
    // ==========================================================
    
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
    
    public boolean updateProfile(String username, String fullname, String phone, String address) {
        String sql = "UPDATE accounts SET fullname = ?, phone = ?, address = ? WHERE username = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullname);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateProfile: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(String username, String newPass) {
        String sql = "UPDATE accounts SET password = ? WHERE username = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi changePassword: " + e.getMessage());
            return false;
        }
    }
    
    public void updateCustomerSpendAndTier(String username, double newOrderTotal) {
        final double KIM_CUONG_TIER = 50000000;
        final double VANG_TIER = 20000000;
        final double BAC_TIER = 5000000;
        Connection conn = null;
        PreparedStatement psGet = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        String sqlGet = "SELECT lifetime_spend, customer_tier FROM accounts WHERE username = ?";
        String sqlUpdate = "UPDATE accounts SET lifetime_spend = ?, customer_tier = ? WHERE username = ?";
        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); 
            psGet = conn.prepareStatement(sqlGet);
            psGet.setString(1, username);
            rs = psGet.executeQuery();
            if (rs.next()) {
                double currentSpend = rs.getDouble("lifetime_spend");
                double newTotalSpend = currentSpend + newOrderTotal;
                String newTier = "dong"; 
                if (newTotalSpend >= KIM_CUONG_TIER) newTier = "kimcuong";
                else if (newTotalSpend >= VANG_TIER) newTier = "vang";
                else if (newTotalSpend >= BAC_TIER) newTier = "bac";
                psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setDouble(1, newTotalSpend);
                psUpdate.setString(2, newTier);
                psUpdate.setString(3, username);
                psUpdate.executeUpdate();
                conn.commit(); 
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chi tiêu & hạng thành viên: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (psGet != null) psGet.close(); } catch (SQLException e) {}
            try { if (psUpdate != null) psUpdate.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    public boolean addAccount(Account acc) {
        String sql = "INSERT INTO accounts (username, password, email, role, fullname, phone, address, customer_tier, lifetime_spend) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getEmail());
            ps.setString(4, acc.getRole());
            ps.setString(5, acc.getFullname());
            ps.setString(6, acc.getPhone());
            ps.setString(7, acc.getAddress());
            ps.setString(8, acc.getCustomerTier());
            ps.setDouble(9, acc.getLifetimeSpend());
            return ps.executeUpdate() > 0; 
        } catch (SQLException e) { System.err.println("Lỗi khi admin thêm tài khoản: " + e.getMessage()); }
        return false;
    }
    
    public boolean updateAccount(Account acc, boolean updatePassword) {
        StringBuilder sql = new StringBuilder("UPDATE accounts SET email = ?, role = ?, fullname = ?, phone = ?, address = ?, customer_tier = ?, lifetime_spend = ? ");
        if (updatePassword) {
            sql.append(", password = ? ");
        }
        sql.append("WHERE username = ?");
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setString(paramIndex++, acc.getEmail());
            ps.setString(paramIndex++, acc.getRole());
            ps.setString(paramIndex++, acc.getFullname());
            ps.setString(paramIndex++, acc.getPhone());
            ps.setString(paramIndex++, acc.getAddress());
            ps.setString(paramIndex++, acc.getCustomerTier());
            ps.setDouble(paramIndex++, acc.getLifetimeSpend());
            if (updatePassword) {
                ps.setString(paramIndex++, acc.getPassword());
            }
            ps.setString(paramIndex++, acc.getUsername());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Lỗi khi admin cập nhật tài khoản: " + e.getMessage()); }
        return false;
    }

    public boolean deleteAccount(String username) {
        Connection conn = null;
        String sqlDeleteCart = "DELETE FROM user_cart_items WHERE username = ?";
        String sqlGetOrderIds = "SELECT id FROM orders WHERE username = ?";
        String sqlDeleteOrderDetails = "DELETE FROM order_details WHERE order_id = ?";
        String sqlDeleteOrders = "DELETE FROM orders WHERE username = ?";
        String sqlDeleteAccount = "DELETE FROM accounts WHERE username = ?";
        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); 
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteCart)) {
                ps.setString(1, username);
                ps.executeUpdate();
            }
            List<Integer> orderIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlGetOrderIds)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        orderIds.add(rs.getInt("id"));
                    }
                }
            }
            if (!orderIds.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteOrderDetails)) {
                    for (int orderId : orderIds) {
                        ps.setInt(1, orderId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteOrders)) {
                ps.setString(1, username);
                ps.executeUpdate();
            }
            int rowsAffected;
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteAccount)) {
                ps.setString(1, username);
                rowsAffected = ps.executeUpdate();
            }
            conn.commit(); 
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi admin xóa tài khoản (cascade): " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}