package Servlet.Order;

import Db.OrderDb;
import Db.VoucherDb; 
import Models.Account;
import Models.Cart;
import Models.Voucher; 
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
        
        // === TÍNH TOÁN LẠI TẤT CẢ CHIẾT KHẤU Ở SERVER (ĐÃ CẬP NHẬT) ===
        
        double subtotal = cart.getTotalMoney();
        
        String tier = acc.getCustomerTier();
        double memberDiscountPercentage = 0;
        if ("kimcuong".equals(tier)) memberDiscountPercentage = 0.1;
        else if ("vang".equals(tier)) memberDiscountPercentage = 0.05;
        else if ("bac".equals(tier)) memberDiscountPercentage = 0.02;
        
        double memberDiscountAmount = subtotal * memberDiscountPercentage;
        double priceAfterMemberDiscount = subtotal - memberDiscountAmount;

        Voucher voucher = (Voucher) session.getAttribute("appliedVoucher");
        double voucherDiscountAmount = 0;
        VoucherDb voucherDb = new VoucherDb();
        
        if (voucher != null) {
            Voucher validVoucher = voucherDb.getValidVoucherByCode(voucher.getCode());
            if (validVoucher != null && priceAfterMemberDiscount >= validVoucher.getMinOrderValue()) {
                if ("percentage".equals(validVoucher.getDiscountType())) {
                    voucherDiscountAmount = priceAfterMemberDiscount * (validVoucher.getDiscountValue() / 100.0);
                } else {
                    voucherDiscountAmount = validVoucher.getDiscountValue();
                }
            }
        }
        
        double finalTotal = priceAfterMemberDiscount - voucherDiscountAmount;
        
        // === SỬA LỖI ÂM TIỀN (LOGIC SERVER) ===
        if (finalTotal < 0) {
            finalTotal = 0;
        }
        // ===================================
        
        OrderDb orderDb = new OrderDb();
        int orderId = orderDb.createOrder(acc, cart, address, phone, finalTotal);
        
        if (orderId != -1) {
            session.removeAttribute("cart"); 
            if (voucher != null) {
                session.removeAttribute("appliedVoucher");
                voucherDb.incrementVoucherUsage(voucher.getCode());
            }
            request.setAttribute("orderId", orderId);
            request.getRequestDispatcher("OrderSuccess.jsp").forward(request, response);
        } else {
            request.setAttribute("checkoutError", "Đặt hàng thất bại, có lỗi CSDL. Vui lòng thử lại.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}