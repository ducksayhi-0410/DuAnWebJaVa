package Servlet.Order;

import Db.OrderDb;
import Models.Account;
import Models.Cart;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = {"/place-order"})
public class PlaceOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        Account acc = (Account) session.getAttribute("acc");
        Cart cart = (Cart) session.getAttribute("cart");
        
        if (acc == null || cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("products");
            return;
        }
        
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        OrderDb orderDb = new OrderDb();
        
        // Gọi hàm tạo đơn hàng (đã khớp với CSDL của bạn)
        int orderId = orderDb.createOrder(acc, cart, address, phone);
        
        if (orderId != -1) {
            // Thành công
            session.removeAttribute("cart"); // Xóa giỏ hàng
            request.setAttribute("orderId", orderId);
            request.getRequestDispatcher("OrderSuccess.jsp").forward(request, response);
        } else {
            // Thất bại
            request.setAttribute("checkoutError", "Đặt hàng thất bại, có lỗi CSDL. Vui lòng thử lại.");
            request.getRequestDispatcher("Checkout.jsp").forward(request, response);
        }
    }
}