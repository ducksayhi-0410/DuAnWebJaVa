package Servlet.Account;

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

@WebServlet(name = "OrderStatusServlet", urlPatterns = {"/order-status"})
public class OrderStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("acc");
        
        // (AuthenticationFilter.java đã bảo vệ trang này, 
        // nhưng chúng ta vẫn cần 'acc' để lấy username)
        if (acc == null) {
            response.sendRedirect("DangNhap.jsp");
            return;
        }
        
        OrderDb db = new OrderDb();
        // Chỉ lấy các đơn hàng "Đang chuẩn bị" hoặc "Đang giao"
        List<Order> processingOrders = db.getProcessingOrdersByUsername(acc.getUsername());
        
        request.setAttribute("processingOrders", processingOrders);
        request.setAttribute("activePage", "status"); // Để highlight menu
        
        request.getRequestDispatcher("order-status.jsp").forward(request, response);
    }
}   