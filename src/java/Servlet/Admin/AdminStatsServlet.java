package Servlet.Admin;

import Db.ReportDb;
import Models.Account;
import Models.OrderDetail;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminStatsServlet", urlPatterns = {"/admin-stats"})
public class AdminStatsServlet extends HttpServlet {

    private boolean checkAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Account acc = (Account) session.getAttribute("acc");
        if (acc == null) return false;
        return "admin".equals(acc.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAdmin(request)) {
            response.sendRedirect("products");
            return;
        }
        
        ReportDb db = new ReportDb();
        
        double totalRevenue = db.getTotalRevenue();
        List<OrderDetail> topProducts = db.getTopSellingProducts();
        List<Account> topCustomers = db.getTopCustomers();
        
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("topProducts", topProducts);
        request.setAttribute("topCustomers", topCustomers);
        
        request.getRequestDispatcher("admin-stats.jsp").forward(request, response);
    }
}