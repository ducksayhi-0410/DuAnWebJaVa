package Servlet.Admin;

import Db.ReportDb;
import Models.Account;
import Models.OrderDetail;
import java.io.IOException;
import java.util.List;
import java.util.Map; // <-- Thêm import
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
        
        // 1. Lấy tổng doanh thu (Đã có)
        double totalRevenue = db.getTotalRevenue();
        
        // 2. Lấy 10 sản phẩm bán chạy (Đã có)
        List<OrderDetail> topProducts = db.getTopSellingProducts();
        
        // 3. Lấy 5 khách hàng chi nhiều nhất (Đã có)
        List<Account> topCustomers = db.getTopCustomers();
        
        // 4. (MỚI) Lấy dữ liệu biểu đồ doanh thu
        Map<String, Double> revenueChartData = db.getMonthlyRevenueStats();
        
        // 5. (MỚI) Lấy dữ liệu biểu đồ đăng ký
        Map<String, Double> registrationChartData = db.getMonthlyRegistrationStats();
        
        // 6. Gửi tất cả qua JSP
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("topProducts", topProducts);
        request.setAttribute("topCustomers", topCustomers);
        request.setAttribute("revenueChartData", revenueChartData); // <-- MỚI
        request.setAttribute("registrationChartData", registrationChartData); // <-- MỚI
        
        request.getRequestDispatcher("admin-stats.jsp").forward(request, response);
    }
}