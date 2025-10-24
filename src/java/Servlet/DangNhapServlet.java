package Servlet;

import Db.AccountDb;
import Db.CartDb; // Thêm import
import Models.Account;
import Models.Cart; // Thêm import
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DangNhapServlet", urlPatterns = {"/dangnhap"})
public class DangNhapServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển người dùng đến trang JSP
        request.getRequestDispatcher("DangNhap.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String loginInput = request.getParameter("email");
        String password = request.getParameter("password");
        
        AccountDb dao = new AccountDb();
        Account foundAccount = dao.checkLogin(loginInput, password);
        
        if (foundAccount != null) {
            HttpSession session = request.getSession();
            session.setAttribute("acc", foundAccount);
            session.setMaxInactiveInterval(30 * 60); 

            // =====================================================
            // BẮT ĐẦU LOGIC GIỎ HÀNG
            // =====================================================

            // 1. Lấy giỏ hàng ẩn danh (nếu có) từ session
            Cart sessionCart = (Cart) session.getAttribute("cart");
            
            // 2. Tải giỏ hàng đã lưu trong CSDL của tài khoản này
            CartDb cartDb = new CartDb();
            Cart dbCart = cartDb.getCartByUsername(foundAccount.getUsername());
            
            // 3. Gộp giỏ hàng session vào giỏ hàng CSDL
            // (Nếu người dùng mua sắm trước khi đăng nhập)
            if (sessionCart != null) {
                dbCart.mergeCart(sessionCart);
                // Sau khi gộp, nên lưu lại vào CSDL ngay
                cartDb.saveCart(foundAccount.getUsername(), dbCart);
            }
            
            // 4. Đặt giỏ hàng đã gộp làm giỏ hàng chính thức
            session.setAttribute("cart", dbCart);

            // =====================================================
            // KẾT THÚC LOGIC GIỎ HÀNG
            // =====================================================

            response.sendRedirect("products"); // Chuyển về trang sản phẩm
        } else {
            request.setAttribute("errorMessage", "Tên đăng nhập, Email hoặc Mật khẩu không đúng!");
            request.getRequestDispatcher("DangNhap.jsp").forward(request, response); 
        }
    }
}