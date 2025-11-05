package Servlet.Order;

import Db.VoucherDb;
import Models.Cart;
import Models.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.DecimalFormat;

@WebServlet(name = "ApplyVoucherServlet", urlPatterns = {"/apply-voucher"})
public class ApplyVoucherServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String code = request.getParameter("code");
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        
        // Lấy tổng tiền đã chiết khấu Hạng (nếu có)
        // (Chúng ta lấy tạm từ cart.getTotalMoney() để đơn giản,
        // logic chuẩn sẽ lấy từ session nếu đã tính ở checkout.jsp)
        double subtotal = cart.getTotalMoney(); 
        
        PrintWriter out = response.getWriter();
        VoucherDb db = new VoucherDb();
        Voucher voucher = db.getValidVoucherByCode(code);
        
        if (cart == null || cart.getItems().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Giỏ hàng trống!\"}");
            out.flush();
            return;
        }

        if (voucher == null) {
            out.print("{\"success\": false, \"message\": \"Mã không hợp lệ, đã hết hạn hoặc hết lượt sử dụng.\"}");
            out.flush();
            return;
        }

        if (subtotal < voucher.getMinOrderValue()) {
            out.print("{\"success\": false, \"message\": \"Đơn hàng chưa đủ " + 
                      String.format("%,.0f", voucher.getMinOrderValue()) + " ₫ để áp dụng.\"}");
            out.flush();
            return;
        }
        
        // Áp dụng thành công
        double discountAmount = 0;
        if ("percentage".equals(voucher.getDiscountType())) {
            discountAmount = subtotal * (voucher.getDiscountValue() / 100.0);
        } else { // fixed_amount
            discountAmount = voucher.getDiscountValue();
            // Đảm bảo không giảm quá tổng tiền
            if(discountAmount > subtotal) discountAmount = subtotal;
        }
        
        // Lưu voucher vào session
        session.setAttribute("appliedVoucher", voucher);
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        // Trả về JSON thành công
        out.print("{\"success\": true, " +
                  "\"message\": \"Áp dụng mã '" + voucher.getCode() + "' thành công!\", " +
                  "\"discountAmount\": " + discountAmount + ", " +
                  "\"discountAmountFormatted\": \"- " + formatter.format(discountAmount) + " ₫\"}");
        out.flush();
    }
}