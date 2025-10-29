package Servlet.Account;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Filter đã kiểm tra đăng nhập
        // Chỉ cần đặt 'activePage' và chuyển tiếp
        request.setAttribute("activePage", "profile");
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}