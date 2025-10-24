package Db;

import Models.Cart;
import Models.Item;
import Models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartDb {

    /**
     * Lấy giỏ hàng của người dùng từ CSDL dựa trên username.
     */
    public Cart getCartByUsername(String username) {
        Cart cart = new Cart();
        ProductDb productDb = new ProductDb(); // Cần ProductDb để lấy thông tin sản phẩm
        String sql = "SELECT * FROM user_cart_items WHERE username = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");
                    
                    // Lấy thông tin sản phẩm đầy đủ
                    Product product = productDb.getProductById(String.valueOf(productId));
                    
                    if (product != null) {
                        Item item = new Item(product, quantity);
                        cart.addItem(item); // Thêm vào đối tượng Cart
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải giỏ hàng từ CSDL: " + e.getMessage());
        }
        return cart;
    }

    /**
     * Lưu giỏ hàng (trong session) vào CSDL.
     * Cách làm: Xóa tất cả item cũ của user, sau đó chèn tất cả item mới.
     */
    public void saveCart(String username, Cart cart) {
        String deleteSql = "DELETE FROM user_cart_items WHERE username = ?";
        String insertSql = "INSERT INTO user_cart_items (username, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = new DBContext().getConnection()) {
            // Tắt tự động commit để thực hiện giao dịch
            conn.setAutoCommit(false);
            
            // 1. Xóa giỏ hàng cũ
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setString(1, username);
                psDelete.executeUpdate();
            }
            
            // 2. Chèn giỏ hàng mới
            if (cart != null && !cart.getItems().isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    for (Item item : cart.getItems()) {
                        psInsert.setString(1, username);
                        psInsert.setInt(2, item.getProduct().getId());
                        psInsert.setInt(3, item.getQuantity());
                        psInsert.addBatch(); // Thêm vào lô
                    }
                    psInsert.executeBatch(); // Thực thi lô
                }
            }
            
            // 3. Commit giao dịch
            conn.commit();
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu giỏ hàng vào CSDL: " + e.getMessage());
            // Cân nhắc rollback ở đây nếu cần
        }
    }

    
}