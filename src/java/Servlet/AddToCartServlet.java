package Servlet;

import Db.ProductDb;
import Models.Cart;
import Models.Item;
import Models.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/add-to-cart"})
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart); // Đặt giỏ hàng mới vào session
        }
        
        String productId = request.getParameter("productId");
        int quantity = 1; // Số lượng mặc định
        
        try {
            // Thử lấy số lượng từ form (hữu ích cho trang chi tiết)
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            // Bỏ qua nếu không có, dùng 1
        }
        
        // Kiểm tra xem đây có phải là yêu cầu AJAX từ trang chủ không
        String isAjax = request.getParameter("isAjax");
        
        try {
            ProductDb productDb = new ProductDb();
            Product product = productDb.getProductById(productId);
            
            boolean success = false;
            String message = "";
            
            if (product != null && quantity > 0) {
                // Kiểm tra số lượng tồn kho
                if (quantity <= product.getQuantity()) {
                    Item newItem = new Item(product, quantity);
                    cart.addItem(newItem);
                    success = true;
                    message = "Đã thêm '" + product.getName() + "' vào giỏ hàng!";
                } else {
                    message = "Số lượng mua vượt quá số lượng tồn kho!";
                }
            } else {
                message = "Sản phẩm không hợp lệ.";
            }
            
            // Nếu là AJAX (từ trang chủ), trả về JSON
            if ("true".equals(isAjax)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                
                // Trả về dữ liệu JSON đơn giản
                out.print("{");
                out.print("\"success\": " + success + ",");
                out.print("\"message\": \"" + message + "\",");
                out.print("\"cartTotalItems\": " + cart.getTotalItems());
                out.print("}");
                out.flush();
                
            } else {
                //
                // =====================================================
                // BẮT ĐẦU SỬA LỖI (THÊM THÔNG BÁO)
                // =====================================================
                //
                if (!success) {
                    session.setAttribute("cartError", message);
                } else {
                    // Gửi thông báo THÀNH CÔNG về session
                    session.setAttribute("cartSuccess", message);
                }
                
                // Kiểm tra xem form được gửi từ đâu
                String source = request.getParameter("source");
                
                if ("index".equals(source)) {
                    // Nếu từ trang chủ (index.jsp), quay về trang chủ
                    response.sendRedirect("products");
                } else {
                    // Nếu từ trang chi tiết (ProductDetail.jsp), quay về trang chi tiết
                    response.sendRedirect("product-detail?productId=" + productId);
                }
                //
                // =====================================================
                // KẾT THÚC SỬA LỖI
                // =====================================================
                //
            }
            
        } catch (Exception e) {
            // Xử lý lỗi chung
            if ("true".equals(isAjax)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"message\": \"Lỗi máy chủ: " + e.getMessage() + "\", \"cartTotalItems\": " + cart.getTotalItems() + "}");
                out.flush();
            } else {
                session.setAttribute("cartError", "Lỗi: " + e.getMessage());
                response.sendRedirect("products");
            }
        }
    }
}