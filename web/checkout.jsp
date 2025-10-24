<%@page import="Models.Account"%>
<%@page import="Models.Item"%>
<%@page import="java.util.List"%>
<%@page import="Models.Cart"%>
<%@page import="java.text.DecimalFormat"%>
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
    
    <style>
        /* CSS riêng cho trang checkout */
        .checkout-container {
            display: flex;
            gap: 30px;
            margin-top: 30px;
        }
        .checkout-form { flex: 2; }
        .checkout-summary {
            flex: 1;
            background: #f9f9f9;
            border: 1px solid #eee;
            border-radius: 5px;
            padding: 20px;
            height: fit-content;
        }
        .checkout-summary h2 { margin-top: 0; }
        .checkout-summary ul { list-style: none; padding: 0; margin: 0; }
        .checkout-summary ul li {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .checkout-summary ul li .item-name {
            color: #333;
            max-width: 200px;
        }
        .checkout-summary ul li .item-name span {
            color: #888;
            font-size: 13px;
        }
        .checkout-summary ul li .item-total {
            color: #555;
            font-weight: 500;
        }
        .checkout-summary .total-row {
            padding-top: 20px;
            font-size: 18px;
            font-weight: bold;
        }
        .checkout-summary .total-row span:last-child {
            color: #a40000;
        }
        
        @media (max-width: 768px) {
            .checkout-container { flex-direction: column-reverse; }
        }
    </style>
</head>
<body>

    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // Filter đã kiểm tra đăng nhập
        Account acc = (Account) session.getAttribute("acc");
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) cart = new Cart(); // Tránh lỗi null
        
        List<Item> items = cart.getItems();
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        String checkoutError = (String) request.getAttribute("checkoutError");
        
        // Lấy thông tin có sẵn của user
        String defaultPhone = (acc.getPhone() != null) ? acc.getPhone() : "";
        String defaultAddress = (acc.getAddress() != null) ? acc.getAddress() : "";
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="cart">Giỏ hàng</a> / <span>Thanh toán</span>
        </div>
        
        <h1>Thanh toán</h1>
        
        <div class="checkout-container">
            <div class="checkout-form">
                <h2>Thông tin giao hàng</h2>
                <p>Vui lòng xác nhận địa chỉ và số điện thoại để giao hàng.</p>
                
                <form action="checkout" method="POST">
                    
                    <% if (checkoutError != null) { %>
                        <div class="cart-notification error"><%= checkoutError %></div>
                    <% } %>

                    <div class="form-group">
                        <label for="fullname">Họ và Tên</label>
                        <input type="text" id="fullname" name="fullname" value="<%= acc.getFullname() %>" readonly style="background-color: #f0f0f0;">
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<%= acc.getEmail() %>" readonly style="background-color: #f0f0f0;">
                    </div>

                    <div class="form-group">
                        <label for="phone">Số điện thoại nhận hàng</label>
                        <input type="tel" id="phone" name="phone" value="<%= defaultPhone %>" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Địa chỉ nhận hàng</label>
                        <input type="text" id="address" name="address" value="<%= defaultAddress %>" required>
                    </div>
                    
                    <hr style="margin: 20px 0; border: 0; border-top: 1px solid #eee;">
                    
                    <h2>Phương thức thanh toán</h2>
                    <p>Thanh toán khi nhận hàng (COD)</p>
                    <p><i>(Chức năng thanh toán online sẽ được cập nhật sau)</i></p>
                    
                    <button type="submit" class="btn-checkout" style="margin-top: 20px;">
                        Xác nhận đặt hàng
                    </button>
                </form>
            </div>
            
            <aside class="checkout-summary">
                <h2>Đơn hàng của bạn</h2>
                <ul>
                    <% for (Item item : items) { %>
                    <li>
                        <div class="item-name">
                            <%= item.getProduct().getName() %>
                            <br>
                            <span>SL: <%= item.getQuantity() %></span>
                        </div>
                        <div class="item-total">
                            <%= formatter.format(item.getProduct().getPrice() * item.getQuantity()) %> ₫
                        </div>
                    </li>
                    <% } %>
                    
                    <li class="total-row">
                        <span>Tổng cộng</span>
                        <span><%= formatter.format(cart.getTotalMoney()) %> ₫</span>
                    </li>
                </ul>
            </aside>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
</body>
</html>