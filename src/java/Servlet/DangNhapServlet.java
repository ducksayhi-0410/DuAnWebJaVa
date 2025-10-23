package Servlet;

import Db.AccountDb;
import Models.Account;
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
        
        // "email" là name của ô input, nó có thể chứa username hoặc email
        String loginInput = request.getParameter("email");
        String password = request.getParameter("password");
        
        AccountDb dao = new AccountDb();
        // Phương thức checkLogin đã được cập nhật để xử lý cả hai
        Account foundAccount = dao.checkLogin(loginInput, password);
        
        if (foundAccount != null) {
            HttpSession session = request.getSession();
            session.setAttribute("acc", foundAccount);
            session.setMaxInactiveInterval(30 * 60); 
            response.sendRedirect("products"); // Chuyển về trang sản phẩm
        } else {
            request.setAttribute("errorMessage", "Tên đăng nhập, Email hoặc Mật khẩu không đúng!");
            request.getRequestDispatcher("DangNhap.jsp").forward(request, response); 
        }
    }
}