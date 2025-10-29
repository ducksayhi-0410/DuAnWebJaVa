package Servlet.Order;

import Models.Cart;
import Models.Item;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UpdateCartServlet", urlPatterns = {"/update-cart"})
public class UpdateCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart != null && !cart.getItems().isEmpty()) {
            
            // Lặp qua tất cả các item trong giỏ hàng
            for (Item item : cart.getItems()) {
                // Lấy tên của ô input, ví dụ: "quantity_1", "quantity_5"
                String inputName = "quantity_" + item.getProduct().getId();
                String quantityStr = request.getParameter(inputName);
                
                try {
                    int newQuantity = Integer.parseInt(quantityStr);
                    
                    // Kiểm tra số lượng hợp lệ (lớn hơn 0 và không vượt quá tồn kho)
                    if (newQuantity > 0 && newQuantity <= item.getProduct().getQuantity()) {
                        // Gọi phương thức update đã tạo trong Cart.java
                        cart.updateItemQuantity(item.getProduct().getId(), newQuantity);
                    } else if (newQuantity <= 0) {
                        // Nếu người dùng nhập 0 hoặc số âm, ta xóa luôn
                        cart.removeItem(item.getProduct().getId());
                    }
                    
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu người dùng nhập chữ
                    System.err.println("Lỗi định dạng số lượng: " + quantityStr);
                }
            }
            
            // Lưu lại giỏ hàng vào session
            session.setAttribute("cart", cart);
        }
        
        // Tải lại trang giỏ hàng
        response.sendRedirect("cart");
    }
}