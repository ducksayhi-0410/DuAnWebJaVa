<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <%
        Product p = (Product) request.getAttribute("product");
        String title = "Không tìm thấy sản phẩm";
        if (p != null) {
            title = p.getName();
        }
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
    %>
    
    <title><%= title %> - Chi tiết sản phẩm</title>
    
    <link rel="stylesheet" href="css/style.css">
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
</head>
<body>
    
    <%-- === SỬA LỖI: Dùng static include (dấu <%@) để chia sẻ biến Java === --%>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <main class="container">
        
        <%
            if (p != null) {
        %>
        
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <span><%= p.getName() %></span>
        </div>
        
        <div class="detail-container">
            <div class="detail-image">
                <img src="<%= p.getImageUrl() %>" alt="<%= p.getName() %>">
            </div>
            
            <div class="detail-info">
                <h1><%= p.getName() %></h1>
                <p class="detail-price"><%= formatter.format(p.getPrice()) %> ₫</p>
                <p class="detail-stock">Số lượng còn lại: <%= p.getQuantity() %></p>
                
                <form action="add-to-cart" method="POST" class="detail-cart-form">
                    <input type="hidden" name="productId" value="<%= p.getId() %>">
                    <label for="quantity">Số lượng:</label>
                    <input type="number" id="quantity" name="quantity" value="1" min="1" max="<%= p.getQuantity() %>">
                    
                    <button type"submit" class="btn-add-cart">
                        <i class="fas fa-shopping-cart"></i> Thêm vào giỏ hàng
                    </button>
                </form>
                
                <div class="detail-description">
                    <p><%= p.getDescription() %></p>
                </div>
                
                <a href="products" class="back-link">&larr; Quay lại trang chủ</a>
            </div>
        </div>

        <%
            } else {
        %>
            <h1>Sản phẩm không tồn tại</h1>
            <p>Sản phẩm bạn đang tìm kiếm có thể đã bị xóa hoặc không có sẵn.</p>
            <a href="products" class="back-link">&larr; Quay lại trang chủ</a>
        <%
            }
        %>
        
    </main>
    
    <%-- === SỬA LỖI: Dùng static include (dấu <%@) === --%>
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
</body>
</html>