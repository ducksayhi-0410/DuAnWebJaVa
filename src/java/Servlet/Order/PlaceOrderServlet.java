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
        
        // ==========================================================
        // === TÍNH TOÁN LẠI CHIẾT KHẤU Ở SERVER (BẢO MẬT) ===
        // ==========================================================
        
        double subtotal = cart.getTotalMoney();
        double finalTotal = subtotal; // Mặc định
        
        String tier = acc.getCustomerTier();
        double discountPercentage = 0;
        
        if ("kimcuong".equals(tier)) {
            discountPercentage = 0.1; // 10%
        } else if ("vang".equals(tier)) {
            discountPercentage = 0.05; // 5%
        } else if ("bac".equals(tier)) {
            discountPercentage = 0.02; // 2%
        }
        
        if (discountPercentage > 0) {
            finalTotal = subtotal * (1 - discountPercentage);
        }
        
        // ==========================================================
        
        OrderDb orderDb = new OrderDb();
        
        // Gọi hàm createOrder đã sửa (truyền finalTotal)
        int orderId = orderDb.createOrder(acc, cart, address, phone, finalTotal);
        
        if (orderId != -1) {
            // Thành công
            session.removeAttribute("cart"); // Xóa giỏ hàng
            request.setAttribute("orderId", orderId);
            request.getRequestDispatcher("OrderSuccess.jsp").forward(request, response);
        } else {
            // Thất bại
            request.setAttribute("checkoutError", "Đặt hàng thất bại, có lỗi CSDL. Vui lòng thử lại.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}