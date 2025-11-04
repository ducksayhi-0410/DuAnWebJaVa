package Servlet.Employee; 

import Db.AccountDb; // <-- Thêm import
import Db.OrderDb;
import Models.Account;
import Models.Order;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EmployeeOrdersServlet", urlPatterns = {"/employee-orders"})
public class EmployeeOrdersServlet extends HttpServlet {

    // (Giữ nguyên hàm checkPermission)
    private boolean checkPermission(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Account acc = (Account) session.getAttribute("acc");
        if (acc == null) return false;
        return "admin".equals(acc.getRole()) || "nhanvien".equals(acc.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // (Giữ nguyên hàm doGet)
        if (!checkPermission(request)) { response.sendRedirect("products"); return; }
        OrderDb db = new OrderDb();
        List<Order> allOrders = db.getAllOrders(); 
        request.setAttribute("allOrders", allOrders);
        request.getRequestDispatcher("employee-orders.jsp").forward(request, response);
    }

    // === CẬP NHẬT HÀM doPost ===
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        if (!checkPermission(request)) {
            response.sendRedirect("products");
            return;
        }
        
        try {
            // Lấy dữ liệu từ form
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String newStatus = request.getParameter("status");
            String username = request.getParameter("username"); // <-- Lấy username
            double totalMoney = Double.parseDouble(request.getParameter("totalMoney")); // <-- Lấy tổng tiền
            
            // 1. Cập nhật trạng thái đơn hàng
            OrderDb orderDb = new OrderDb();
            boolean updateSuccess = orderDb.updateOrderStatus(orderId, newStatus); 
            
            // 2. KÍCH HOẠT NÂNG HẠNG
            // Nếu cập nhật thành công VÀ trạng thái là "Giao hàng thành công"
            if (updateSuccess && "Giao hàng thành công".equals(newStatus)) {
                AccountDb accountDb = new AccountDb();
                accountDb.updateCustomerSpendAndTier(username, totalMoney);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse ID hoặc Tiền: " + e.getMessage());
        }
        
        response.sendRedirect("employee-orders"); 
    }
}