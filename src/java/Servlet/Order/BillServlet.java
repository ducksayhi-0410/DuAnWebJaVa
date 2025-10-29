package Servlet.Order;

import Db.OrderDb;
import Models.Account;
import Models.Order;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "BillServlet", urlPatterns = {"/export-bill"})
public class BillServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("acc");
        String orderIdStr = request.getParameter("orderId");
        
        // Bảo vệ: Cần đăng nhập và phải có orderId
        if (acc == null || orderIdStr == null) {
            response.sendRedirect("DangNhap.jsp");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDb db = new OrderDb();
            
            // Lấy đơn hàng, hàm này đã kiểm tra 'username'
            Order order = db.getOrderByIdAndUser(orderId, acc.getUsername());
            
            if (order != null) {
                // Nếu tìm thấy đơn hàng (và nó là của user này)
                request.setAttribute("order", order);
                request.getRequestDispatcher("Bill.jsp").forward(request, response);
            } else {
                // Nếu không tìm thấy (hoặc đơn hàng của người khác)
                response.sendRedirect("products"); // Về trang chủ
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("products"); // Nếu orderId không phải là số
        }
    }
}