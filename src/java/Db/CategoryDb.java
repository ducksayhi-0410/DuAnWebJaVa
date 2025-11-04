package Db;

import Models.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDb {

    /**
     * Lấy tất cả các danh mục từ CSDL. (Bạn đã có)
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        // Sắp xếp theo tên để hiển thị đẹp hơn
        String sql = "SELECT * FROM categories ORDER BY name ASC"; 
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("id"),
                    rs.getString("name")
                );
                categoryList.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách danh mục: " + e.getMessage());
        }
        return categoryList;
    }

    // ==========================================================
    // === CÁC PHƯƠNG THỨC CÒN THIẾU GÂY LỖI TRONG ẢNH ===
    // ==========================================================

    /**
     * Thêm một danh mục mới.
     * (Fix lỗi cho hàm handleAddCategory)
     */
    public boolean addCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm danh mục: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa một danh mục.
     * (Fix lỗi cho hàm handleDeleteCategory)
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            // Lỗi này HẦU HẾT xảy ra do vi phạm ràng buộc khóa ngoại
            // (có sản phẩm đang thuộc danh mục này)
            System.err.println("Lỗi khi xóa danh mục (có thể do khóa ngoại): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy một danh mục theo ID (Dùng cho chức năng Sửa sau này).
     */
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh mục theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cập nhật tên một danh mục (Dùng cho chức năng Sửa sau này).
     */
    public boolean updateCategory(int id, String name) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật danh mục: " + e.getMessage());
            return false;
        }
    }
}