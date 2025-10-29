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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>

    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // SỬA LỖI: Xóa khai báo "Account acc" và "Cart cart" bị trùng
        // Các biến này đã được tạo trong main-header.jspf
        
        // Chốt an toàn (Vì header đã tạo 'acc', ta chỉ cần kiểm tra 'cart')
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart"); // Nếu giỏ hàng trống, về trang giỏ
            return;
        }
        
        List<Item> items = cart.getItems();
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        String checkoutError = (String) request.getAttribute("checkoutError");
        
        // Lấy thông tin có sẵn của user (biến 'acc' đã tồn tại)
        String defaultPhone = (acc.getPhone() != null) ? acc.getPhone() : "";
        String defaultAddress = (acc.getAddress() != null) ? acc.getAddress() : "";
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="cart">Giỏ hàng</a> / <span>Thanh toán</span>
        </div>
        
        <h1>Chi tiết thanh toán</h1>
        
        <form action="place-order" method="POST">
            <div class="checkout-container">
                <div class="customer-details">
                    <h2>Thông tin khách hàng</h2>
                    
                    <% if (checkoutError != null) { %>
                        <div style="color: red; background: #ffe0e0; border: 1px solid red; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                            <%= checkoutError %>
                        </div>
                    <% } %>

                    <div class="form-group">
                        <label for="fullname">Họ và Tên</label>
                        <input type="text" id="fullname" name="fullname" value="<%= acc.getFullname() %>" required readonly style="background-color: #f0f0f0;">
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<%= acc.getEmail() %>" required readonly style="background-color: #f0f0f0;">
                    </div>
                    <div class="form-group">
                        <label for="phone">Số điện thoại giao hàng</label>
                        <input type="tel" id="phone" name="phone" value="<%= defaultPhone %>" required>
                    </div>
                    <div class="form-group">
                        <label for="address">Địa chỉ giao hàng</label>
                        <input type="text" id="address" name="address" value="<%= defaultAddress %>" placeholder="Nhập địa chỉ của bạn..." required>
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
                            <tr class="total-row">
                                <td><strong>Tổng cộng</strong></td>
                                <td><strong><%= formatter.format(cart.getTotalMoney()) %> ₫</strong></td>
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