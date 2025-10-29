package Servlet.Account;

import Db.AccountDb; // Cần import AccountDb
import Models.Account;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EditProfileServlet", urlPatterns = {"/edit-profile"})
public class EditProfileServlet extends HttpServlet {

    // Hiển thị trang form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("activePage", "edit");
        request.getRequestDispatcher("edit-profile.jsp").forward(request, response);
    }

    // Xử lý cập nhật thông tin
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        // Lấy thông tin từ form
        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        // Lấy tài khoản hiện tại từ session
        Account acc = (Account) session.getAttribute("acc");
        
        // Gọi DAO để cập nhật CSDL
        AccountDb db = new AccountDb();
        boolean success = db.updateProfile(acc.getUsername(), fullname, phone, address);
        
        if (success) {
            // Cập nhật lại thông tin trong session ngay lập tức
            acc.setFullname(fullname);
            acc.setPhone(phone);
            acc.setAddress(address);
            session.setAttribute("acc", acc); // Đặt lại đối tượng 'acc' đã cập nhật
            
            request.setAttribute("success", "Cập nhật thông tin thành công!");
        } else {
            request.setAttribute("error", "Cập nhật thất bại, có lỗi xảy ra!");
        }
        
        // Đặt lại activePage và forward về trang edit
        request.setAttribute("activePage", "edit");
        request.getRequestDispatcher("edit-profile.jsp").forward(request, response);
    }
}