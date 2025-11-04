<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.Cart"%>
<%@page import="Models.Account"%>
<%@page import="Models.Item"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán</title>
    <link rel="stylesheet" href="css/style.css">
    </head>
<body>

    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // (acc và cart đã được lấy từ main-header.jspf)
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart");
            return;
        }
        
        List<Item> items = cart.getItems();
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String checkoutError = (String) request.getAttribute("checkoutError");
        
        String defaultPhone = (acc.getPhone() != null) ? acc.getPhone() : "";
        String defaultAddress = (acc.getAddress() != null) ? acc.getAddress() : "";
        
        // ==========================================================
        // === LOGIC CHIẾT KHẤU MỚI ===
        // ==========================================================
        
        // 1. Lấy tổng tiền gốc
        double subtotal = cart.getTotalMoney();
        
        // 2. Xác định chiết khấu
        String tier = acc.getCustomerTier();
        String tierName = "Đồng";
        double discountPercentage = 0;
        
        if ("kimcuong".equals(tier)) {
            tierName = "Kim Cương";
            discountPercentage = 0.1; // 10%
        } else if ("vang".equals(tier)) {
            tierName = "Vàng";
            discountPercentage = 0.05; // 5%
        } else if ("bac".equals(tier)) {
            tierName = "Bạc";
            discountPercentage = 0.02; // 2%
        }
        
        // 3. Tính toán
        double discountAmount = subtotal * discountPercentage;
        double finalTotal = subtotal - discountAmount;
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="cart">Giỏ hàng</a> / <span>Thanh toán</span>
        </div>
        
        <h1>Chi tiết thanh toán</h1>
        
        <form action="place-order" method="POST">
            <div class="checkout-container">
 
                <div class="customer-details">
                    <h2>Thông tin khách hàng (Hạng: <%= tierName %>)</h2>
                    
                    <% if (checkoutError != null) { %>
                         <div style="color: red; background: #ffe0e0;"><%= checkoutError %></div>
                    <% } %>
                    
                    <div class="form-group">
                        <label for="fullname">Họ và Tên</label>
                        <input type="text" id="fullname" name="fullname" value="<%= acc.getFullname() %>" readonly>
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<%= acc.getEmail() %>" readonly>
                    </div>
                    <div class="form-group">
                        <label for="phone">Số điện thoại giao hàng</label>
                        <input type="tel" id="phone" name="phone" value="<%= defaultPhone %>" required>
                    </div>
                    <div class="form-group">
                        <label for="address">Địa chỉ giao hàng</label>
                        <input type="text" id="address" name="address" value="<%= defaultAddress %>" required>
                    </div>
                </div>

                <div class="order-summary">
                    <h2>Đơn hàng của bạn</h2>
                    <table class="order-table">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Tạm tính</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Item item : items) { %>
                            <tr>
                                <td><%= item.getProduct().getName() %> <strong>× <%= item.getQuantity() %></strong></td>
                                <td><%= formatter.format(item.getProduct().getPrice() * item.getQuantity()) %> ₫</td>
                            </tr>
                            <% } %>
                            
                            <tr class="total-row" style="font-size: 16px; font-weight: normal;">
                                <td><strong>Tạm tính</strong></td>
                                <td><strong><%= formatter.format(subtotal) %> ₫</strong></td>
                            </tr>
                            
                            <% if (discountAmount > 0) { %>
                            <tr class="total-row" style="font-size: 16px; font-weight: normal; color: #a40000;">
                                <td><strong>Chiết khấu (Hạng <%= tierName %> - <%= discountPercentage * 100 %>%)</strong></td>
                                <td><strong>- <%= formatter.format(discountAmount) %> ₫</strong></td>
                            </tr>
                            <% } %>
                            
                            <tr class="total-row">
                                <td><strong>Tổng cộng</strong></td>
                                <td><strong><%= formatter.format(finalTotal) %> ₫</strong></td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <h3 style="margin-top: 20px;">Phương thức thanh toán</h3>
                    <p>Thanh toán khi nhận hàng (COD)</p>
                    
                    <button type="submit" class="btn-checkout" style="margin-top: 20px;">
                        XÁC NHẬN ĐẶT HÀNG
                    </button>
                </div>
            </div>
        </form>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>