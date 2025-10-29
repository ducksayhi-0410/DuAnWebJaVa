package Servlet.Account; // Hoặc package bạn đang dùng

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

@WebServlet(name = "MyOrdersServlet", urlPatterns = {"/my-orders"})
public class MyOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("acc");
        
        // Bảo vệ: Nếu chưa đăng nhập, đá về trang chủ
        if (acc == null) {
            response.sendRedirect("products");
            return;
        }
        
        // 1. Lấy dữ liệu
        OrderDb db = new OrderDb();
        List<Order> orderList = db.getOrdersByUsername(acc.getUsername());
        
        // 2. Gửi dữ liệu qua JSP
        request.setAttribute("orderList", orderList);
        
        // 3. Chuyển tiếp tới trang view
        request.getRequestDispatcher("my-orders.jsp").forward(request, response);
    }
}