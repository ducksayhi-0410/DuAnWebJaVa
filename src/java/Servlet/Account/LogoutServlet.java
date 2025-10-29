package Servlet.Account;

import Db.CartDb; // Thêm import
import Models.Account; // Thêm import
import Models.Cart; // Thêm import
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); 
        
        if (session != null) {
            // =====================================================
            // BẮT ĐẦU LOGIC LƯU GIỎ HÀNG
            // =====================================================
            
            // Lấy tài khoản và giỏ hàng TỪ SESSION
            Account acc = (Account) session.getAttribute("acc");
            Cart cart = (Cart) session.getAttribute("cart");

            // Nếu người dùng đã đăng nhập VÀ có giỏ hàng
            if (acc != null && cart != null) {
                CartDb cartDb = new CartDb();
                // Lưu giỏ hàng hiện tại vào CSDL
                cartDb.saveCart(acc.getUsername(), cart);
            }
            
            // =====================================================
            // KẾT THÚC LOGIC LƯU GIỎ HÀNG
            // =====================================================
            
            session.invalidate(); // Xóa session SAU KHI ĐÃ LƯU
        }
        
        response.sendRedirect("products"); 
    }
}