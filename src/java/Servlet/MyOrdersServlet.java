package Servlet;

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
        
        // Filter đã kiểm tra đăng nhập, nên ta yên tâm lấy 'acc'
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("acc");
        
        // 1. Gọi DAO để lấy danh sách đơn hàng
        OrderDb orderDb = new OrderDb();
        List<Order> orderList = orderDb.getOrdersByUsername(acc.getUsername());
        
        // 2. Gửi danh sách đơn hàng sang JSP
        request.setAttribute("orderList", orderList);
        
        // 3. Đặt 'activePage' cho menu
        request.setAttribute("activePage", "orders");
        
        // 4. Chuyển tiếp đến trang JSP mới
        request.getRequestDispatcher("my-orders.jsp").forward(request, response);
    }
}