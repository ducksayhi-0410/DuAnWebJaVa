package Servlet.Employee; 

import Db.AccountDb; 
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
        List<Order> allOrders = db.getAllOrders(); 
        
        request.setAttribute("allOrders", allOrders);
        request.getRequestDispatcher("employee-orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        if (!checkPermission(request)) {
            response.sendRedirect("products");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String newStatus = request.getParameter("status");
            String username = request.getParameter("username"); 
            double totalMoney = Double.parseDouble(request.getParameter("totalMoney"));
            
            OrderDb orderDb = new OrderDb();
            
            Order currentOrder = orderDb.getOrderById(orderId);
            if (currentOrder == null) {
                response.sendRedirect("employee-orders");
                return;
            }
            String oldStatus = currentOrder.getStatus();
            
            orderDb.updateOrderStatus(orderId, newStatus); 
            
            if ("Giao hàng thành công".equals(newStatus) && !"Giao hàng thành công".equals(oldStatus)) {
                AccountDb accountDb = new AccountDb();
                
                // === SỬA LỖI LOGIC TẠI ĐÂY ===
                // Truyền 'totalMoney' (số tiền) chứ không phải 'newStatus' (cái chữ)
                accountDb.updateCustomerSpendAndTier(username, totalMoney);
                // === KẾT THÚC SỬA LỖI ===
                
                HttpSession session = request.getSession(false);
                if(session != null) {
                    Account loggedInAcc = (Account) session.getAttribute("acc");
                    if(loggedInAcc != null && loggedInAcc.getUsername().equals(username)) {
                        loggedInAcc = new AccountDb().getAccountByUsername(username);
                        session.setAttribute("acc", loggedInAcc);
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse ID hoặc Tiền: " + e.getMessage());
        }
        
        response.sendRedirect("employee-orders"); 
    }
}