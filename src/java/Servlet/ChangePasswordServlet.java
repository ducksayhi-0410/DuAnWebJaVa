package Servlet;

import Db.AccountDb; // Cần import AccountDb
import Models.Account;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    // Hiển thị trang form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("activePage", "password");
        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }

    // Xử lý đổi mật khẩu
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("acc");
        
        // Lấy dữ liệu form
        String oldPass = request.getParameter("old_password");
        String newPass = request.getParameter("new_password");
        String reNewPass = request.getParameter("re_new_password");

        // 1. Kiểm tra mật khẩu cũ
        // Giả sử đối tượng 'acc' trong session có chứa mật khẩu
        if (!acc.getPassword().equals(oldPass)) {
            request.setAttribute("error", "Mật khẩu cũ không chính xác!");
            request.setAttribute("activePage", "password");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return; // Dừng lại
        }
        
        // 2. Kiểm tra mật khẩu mới có khớp không
        if (!newPass.equals(reNewPass)) {
            request.setAttribute("error", "Mật khẩu nhập lại không khớp!");
            request.setAttribute("activePage", "password");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return; // Dừng lại
        }
        
        // 3. Gọi DAO để cập nhật CSDL
        AccountDb db = new AccountDb();
        boolean success = db.changePassword(acc.getUsername(), newPass);
        
        if (success) {
            // Cập nhật lại mật khẩu trong session
            acc.setPassword(newPass);
            session.setAttribute("acc", acc);
            
            request.setAttribute("success", "Đổi mật khẩu thành công!");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra, không thể đổi mật khẩu!");
        }
        
        request.setAttribute("activePage", "password");
        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }
}