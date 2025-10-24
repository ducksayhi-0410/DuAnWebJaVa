package Db;

import Models.Product; // Đảm bảo bạn đã cập nhật Product.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDb {

    public List<Product> getProductsFiltered(String categoryId, String searchQuery, String minPrice, String maxPrice, String sortOrder) {
        List<Product> productList = new ArrayList<>();
        List<Object> params = new ArrayList<>(); 

        // Câu SQL không đổi, vì "SELECT *" đã bao gồm cột 'manufacturer' mới
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");

        // 1. Lọc theo Danh mục
        if (categoryId != null && !categoryId.isEmpty()) {
            sql.append(" AND categoryId = ?");
            params.add(categoryId);
        }
        // 2. Lọc theo Tìm kiếm (tìm theo Tên)
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + searchQuery + "%");
        }
        // 3. Lọc theo Giá
        if (minPrice != null && !minPrice.isEmpty()) {
            sql.append(" AND price >= ?");
            params.add(Double.parseDouble(minPrice));
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            sql.append(" AND price <= ?");
            params.add(Double.parseDouble(maxPrice));
        }
        // 4. Sắp xếp
        if ("price-asc".equals(sortOrder)) {
            sql.append(" ORDER BY price ASC");
        } else if ("price-desc".equals(sortOrder)) {
            sql.append(" ORDER BY price DESC");
        } else {
            sql.append(" ORDER BY id DESC"); // Mặc định
        }

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // === SỬA ĐỔI CHÍNH (THÊM HÃNG SẢN XUẤT) ===
                    productList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("imageUrl"),
                        rs.getInt("categoryId"),
                        rs.getInt("quantity"),
                        rs.getString("manufacturer") // <-- THÊM DÒNG NÀY
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc sản phẩm: " + e.getMessage());
        }
        return productList;
    }

    // --- Phương thức này không cần sửa, vì nó chỉ đếm (COUNT) ---
    public int getProductCount(String categoryId, String searchQuery, String minPrice, String maxPrice) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products WHERE 1=1");

        if (categoryId != null && !categoryId.isEmpty()) {
            sql.append(" AND categoryId = ?");
            params.add(categoryId);
        }
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + searchQuery + "%");
        }
        if (minPrice != null && !minPrice.isEmpty()) {
            sql.append(" AND price >= ?");
            params.add(Double.parseDouble(minPrice));
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            sql.append(" AND price <= ?");
            params.add(Double.parseDouble(maxPrice));
        }

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm sản phẩm: " + e.getMessage());
        }
        return 0;
    }

    // --- PHƯƠNG THỨC LẤY 1 SẢN PHẨM ---
    public Product getProductById(String productId) {
        // Câu SQL không đổi, vì "SELECT *" đã bao gồm cột 'manufacturer' mới
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, productId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // === SỬA ĐỔI CHÍNH (THÊM HÃNG SẢN XUẤT) ===
                    return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("imageUrl"),
                        rs.getInt("categoryId"),
                        rs.getInt("quantity"),
                        rs.getString("manufacturer") // <-- THÊM DÒNG NÀY
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy sản phẩm theo ID: " + e.getMessage());
        }
        return null; // Trả về null nếu không tìm thấy
    }
}