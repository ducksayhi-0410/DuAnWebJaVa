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
    <title>Giỏ hàng của bạn</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
    <style>
        .cart-container { padding-top: 30px; }
        .cart-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        .cart-table th, .cart-table td { border: 1px solid #eee; padding: 15px; text-align: left; }
        .cart-table th { background-color: #f9f9f9; }
        .cart-table td.product-thumbnail img { width: 80px; height: auto; }
        .cart-table td.product-name a { color: #a40000; text-decoration: none; font-weight: 500;}
        .cart-table td.product-quantity input { width: 60px; text-align: center; padding: 5px; }
        .cart-table td.product-remove a { color: red; font-size: 18px; text-decoration: none; }
        .cart-total { 
            margin-top: 30px; 
            width: 40%; 
            margin-left: auto; 
            border: 2px solid #a40000; 
            padding: 20px; 
            background: #fdfdfd; 
        }
        .cart-total h2 { margin-top: 0; }
        .cart-total p { display: flex; justify-content: space-between; font-size: 16px; margin: 15px 0; }
        .cart-total p.total-price { font-size: 20px; font-weight: bold; color: #a40000; }
        .btn-checkout {
            display: block;
            width: 100%;
            padding: 15px;
            background-color: #a40000;
            color: white;
            text-align: center;
            text-decoration: none;
            font-weight: bold;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
</head>
<body>

    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <span>Giỏ hàng</span>
        </div>
        
        <div class="cart-container">
            <h1>Giỏ hàng</h1>
            
            <%
                // === SỬA LỖI: XÓA DÒNG "Cart cart = ..." ===
                // Biến 'cart' đã được tạo trong main-header.jspf
                
                DecimalFormat formatter = new DecimalFormat("###,###,###");
                
                if (cart == null || cart.getTotalItems() == 0) {
            %>
                <p>Giỏ hàng của bạn đang trống.</p>
                <a href="products" class="btn-detail" style="display:inline-block; background-color:#a40000; color:white;">Bắt đầu mua sắm</a>
            <%
                } else {
                    List<Item> items = cart.getItems();
            %>
            
            <form action="update-cart" method="POST">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>&nbsp;</th>
                            <th>Hình ảnh</th>
                            <th>Sản phẩm</th>
                            <th>Giá</th>
                            <th>Số lượng</th>
                            <th>Tạm tính</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Item item : items) { %>
                        <tr>
                            <td class="product-remove">
                                <a href="remove-cart-item?productId=<%= item.getProduct().getId() %>" title="Xóa sản phẩm này">×</a>
                            </td>
                            <td class="product-thumbnail">
                                <img src="<%= item.getProduct().getImageUrl() %>" alt="<%= item.getProduct().getName() %>">
                            </td>
                            <td class="product-name">
                                <a href="product-detail?productId=<%= item.getProduct().getId() %>"><%= item.getProduct().getName() %></a>
                            </td>
                            <td class="product-price">
                                <%= formatter.format(item.getProduct().getPrice()) %> D
                            </td>
                            <td class="product-quantity">
                                <input type="number" name="quantity_<%= item.getProduct().getId() %>" value="<%= item.getQuantity() %>" min="1" max="<%= item.getProduct().getQuantity() %>">
                            </td>
                            <td class="product-subtotal">
                                <%= formatter.format(item.getProduct().getPrice() * item.getQuantity()) %> ₫
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <button type="submit" class="btn-detail" style="margin-top: 15px;">Cập nhật giỏ hàng</button>
            </form>
            
            <div class="cart-total">
                <h2>Tổng cộng giỏ hàng</h2>
                <p>
                    <span>Tạm tính:</span>
                    <span><%= formatter.format(cart.getTotalMoney()) %> ₫</span>
                </p>
                <p class="total-price">
                    <span>Tổng cộng:</span>
                    <span><%= formatter.format(cart.getTotalMoney()) %> ₫</span>
                </p>
                
                <a href="checkout" class="btn-checkout">Tiến hành thanh toán</a>
            </div>
            
            <%
                }
            %>
            
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
</body>
</html>