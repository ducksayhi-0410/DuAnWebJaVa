package Db;

import Models.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDb {

    // Phương thức 1: Đăng nhập bằng USERNAME hoặc EMAIL
    public Account checkLogin(String loginInput, String password) {
        String sql = "SELECT * FROM accounts WHERE (username = ? OR email = ?) AND password = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, loginInput); // Kiểm tra username
            ps.setString(2, loginInput); // Kiểm tra email
            ps.setString(3, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("fullname"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra đăng nhập: " + e.getMessage());
        }
        return null;
    }
    
    public boolean checkStaffRole ( ) {
        String sql = "SELECT * FROM accounts";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

             try (ResultSet rs = ps.executeQuery()) {
                 while ( rs.next()) {
                     if ( rs.getString("role") == "nhanvien") {
                         return true;
                     }
                 }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra username: " + e.getMessage());
        }
        return false;
        }
    // Phương thức 2: Kiểm tra USERNAME đã tồn tại
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE username = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra username: " + e.getMessage());
        }
        return false;
    }

    // Phương thức 3: Kiểm tra EMAIL đã tồn tại
    public boolean checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE email = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra email: " + e.getMessage());
        }
        return false;
    }

    // Phương thức 4: Kiểm tra SĐT đã tồn tại
    public boolean checkPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE phone = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra SĐT: " + e.getMessage());
        }
        return false;
    }
    
    // Phương thức 5: Tạo tài khoản mới
    public boolean createAccount(String username, String email, String password, String fullname, String phone) {
        String sql = "INSERT INTO accounts (username, email, password, role, fullname, phone) VALUES (?, ?, ?, 'customer', ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, fullname);
            ps.setString(5, phone);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; 
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo tài khoản: " + e.getMessage());
        }
        return false;
    }

    public boolean changePassword(String username, String newPass) {
        String sql = "UPDATE accounts SET password=? WHERE username=?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPass);
            ps.setString(2, username);
            
            ps.executeUpdate();
            
            return ps.executeUpdate() > 0;
           
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi thay đổi password " + e.getMessage());
        }
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean updateProfile(String username, String fullname, String phone, String address) {
        String sql = "UPDATE accounts SET username=?, fullname=?, phone=?, address=? WHERE username=?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, fullname);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, username);
            
            ps.executeUpdate();
            
            return ps.executeUpdate() > 0;
           
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi thay đổi thông tin người dùng " + e.getMessage());
        }
        return false;
        //throw new UnsupportedOperationException("Not supported yet"); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}