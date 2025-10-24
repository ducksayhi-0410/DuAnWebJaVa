package Servlet;

import Db.CartDb;
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

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    // Hiển thị trang xác nhận thông tin
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        
        // Nếu giỏ hàng trống, đá về trang giỏ hàng
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart");
            return;
        }
        
        // Chuyển tiếp đến trang JSP
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    // Xử lý logic đặt hàng
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("acc");
        Cart cart = (Cart) session.getAttribute("cart");
        
        // Lấy thông tin giao hàng từ form
        // (Chúng ta dùng thông tin này thay vì thông tin trong profile,
        // vì người dùng có thể muốn giao đến địa chỉ khác)
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");

        // Kiểm tra lần cuối
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart");
            return;
        }
        
        // 1. Gọi DAO để tạo đơn hàng
        OrderDb orderDb = new OrderDb();
        boolean success = orderDb.createOrder(acc, cart, address, phone);
        
        if (success) {
            // 2. Nếu thành công, xóa giỏ hàng khỏi session
            session.removeAttribute("cart");
            
            // 3. Xóa giỏ hàng trong CSDL (lưu một giỏ hàng rỗng)
            CartDb cartDb = new CartDb();
            cartDb.saveCart(acc.getUsername(), new Cart());
            
            // 4. Chuyển đến trang báo thành công
            response.sendRedirect("order-success");
            
        } else {
            // 5. Nếu thất bại (thường là do hết hàng)
            // Gửi lỗi và quay lại trang checkout
            request.setAttribute("checkoutError", "Đặt hàng thất bại. Có thể một số sản phẩm đã hết hàng. Vui lòng kiểm tra lại giỏ hàng.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}