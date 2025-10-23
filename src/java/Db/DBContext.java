package Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {
    
    // --- ĐÃ CẬP NHẬT TÊN DATABASE ---
    private final String DB_URL = "jdbc:mysql://localhost:3306/qlruouvang?useSSL=false"; 
    private final String DB_USER = "root";
    // Mật khẩu root của XAMPP mặc định là RỖNG ""
    private final String DB_PASSWORD = ""; 
    private final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    // --- KẾT THÚC CẬP NHẬT ---

    public Connection getConnection() {
        Connection conn = null;
        try {
            // 1. Nạp Driver
            Class.forName(DB_DRIVER);
            // 2. Mở kết nối
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Dòng này để kiểm tra kết nối thành công (bạn có thể xóa sau)
            System.out.println("Kết nối CSDL 'qlruouvang' thành công!"); 
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Lỗi kết nối CSDL: " + ex.getMessage());
        }
        return conn;
    }
}