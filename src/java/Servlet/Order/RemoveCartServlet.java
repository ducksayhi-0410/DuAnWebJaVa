package Servlet.Order;

import Models.Cart;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RemoveCartServlet", urlPatterns = {"/remove-cart-item"})
public class RemoveCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart != null) {
            try {
                // Lấy ID sản phẩm từ URL
                int productId = Integer.parseInt(request.getParameter("productId"));
                
                // Gọi phương thức remove đã tạo trong Cart.java
                cart.removeItem(productId);
                
                // Lưu lại giỏ hàng
                session.setAttribute("cart", cart);
                
            } catch (NumberFormatException e) {
                System.err.println("Lỗi ID sản phẩm không hợp lệ.");
            }
        }
        
        // Tải lại trang giỏ hàng
        response.sendRedirect("cart");
    }
}