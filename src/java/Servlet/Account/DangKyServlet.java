package Servlet.Account;

import Db.AccountDb;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DangKyServlet", urlPatterns = {"/dangky"})
public class DangKyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("DangKy.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String email = request.getParameter("email"); 
        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String re_password = request.getParameter("re_password");

        if (!password.equals(re_password)) {
            request.setAttribute("error", "Mật khẩu nhập lại không khớp!");
            request.getRequestDispatcher("DangKy.jsp").forward(request, response);
            return; 
        }
        
        AccountDb db = new AccountDb();
        
        if (db.checkUsernameExists(username)) {
            request.setAttribute("error", "Tên đăng nhập này đã được sử dụng!");
            request.getRequestDispatcher("DangKy.jsp").forward(request, response);
            return; 
        }
        
        if (db.checkEmailExists(email)) {
            request.setAttribute("error", "Email này đã được sử dụng!");
            request.getRequestDispatcher("DangKy.jsp").forward(request, response);
            return; 
        }

        if (db.checkPhoneExists(phone)) {
            request.setAttribute("error", "Số điện thoại này đã được sử dụng!");
            request.getRequestDispatcher("DangKy.jsp").forward(request, response);
            return; 
        }
        
        boolean success = db.createAccount(username, email, password, fullname, phone);
        
        if (success) {
            response.sendRedirect("DangNhap.jsp?register=success");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            request.getRequestDispatcher("DangKy.jsp").forward(request, response);
        }
    }
}