package Servlet.Employee; 

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
        
        if (!checkPermission(request)) {
            response.sendRedirect("products");
            return;
        }
        
        OrderDb db = new OrderDb();
        List<Order> allOrders = db.getAllOrders(); // Lấy TẤT CẢ đơn hàng
        
        request.setAttribute("allOrders", allOrders);
        request.getRequestDispatcher("employee-orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkPermission(request)) {
            response.sendRedirect("products");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String newStatus = request.getParameter("status");
            
            OrderDb db = new OrderDb();
            db.updateOrderStatus(orderId, newStatus); // Cập nhật trạng thái
            
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse ID đơn hàng: " + e.getMessage());
        }
        
        response.sendRedirect("employee-orders"); 
    }
}