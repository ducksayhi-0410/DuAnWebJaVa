package Servlet.Order;

import Models.Account;
import Models.Cart;
import java.io.IOException;
// ĐẢM BẢO LÀ "jakarta"
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // 1. Kiểm tra phiên đăng nhập
        Account acc = (Account) session.getAttribute("acc");
        
        if (acc == null) {
            // Nếu CHƯA đăng nhập, chuyển về trang đăng nhập
            response.sendRedirect("DangNhap.jsp");
            return;
        }
        
        // 2. Kiểm tra giỏ hàng
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            // Nếu giỏ hàng trống, quay lại trang giỏ hàng
            response.sendRedirect("cart");
            return;
        }
        
        // 3. Nếu OK, chuyển tiếp đến trang Checkout.jsp
        request.getRequestDispatcher("Checkout.jsp").forward(request, response);
    }
}