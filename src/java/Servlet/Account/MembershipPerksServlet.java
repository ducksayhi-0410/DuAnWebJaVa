package Servlet.Account;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Filter "AuthenticationFilter" sẽ tự động bảo vệ trang này
@WebServlet(name = "MembershipPerksServlet", urlPatterns = {"/membership-perks"})
public class MembershipPerksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đặt 'activePage' để highlight menu
        request.setAttribute("activePage", "perks");
        request.getRequestDispatcher("membership-perks.jsp").forward(request, response);
    }
}