package Servlet.Order; // Hoặc package bạn đang dùng

import Models.Account;
import Models.Cart;
import java.io.IOException;
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
        
        Account acc = (Account) session.getAttribute("acc");
        
        if (acc == null) {
            response.sendRedirect("DangNhap.jsp");
            return;
        }
        
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart");
            return;
        }
        
        // === SỬA LỖI Ở ĐÂY ===
        // Đổi "Checkout.jsp" (viết hoa) thành "checkout.jsp" (viết thường)
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }
}