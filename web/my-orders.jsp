<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.OrderDetail"%>
<%@page import="Models.Order"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử mua hàng</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // Lấy danh sách đơn hàng từ Servlet
        List<Order> orderList = (List<Order>) request.getAttribute("orderList");
        
        // Chuẩn bị định dạng
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm 'ngày' dd/MM/yyyy");
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <span>Lịch sử mua hàng</span>
        </div>
        
        <div class="account-container">
            <%-- Gọi file nav mới của bạn --%>
            <%@ include file="WEB-INF/account-nav.jspf" %>
            
            <section class="account-content">
                <h1>Lịch sử mua hàng</h1>
                
                <div class="order-list">
                    <%
                        if (orderList == null || orderList.isEmpty()) {
                    %>
                        <p>Bạn chưa có đơn hàng nào.</p>
                    <%
                        } else {
                            // Lặp qua từng đơn hàng
                            for (Order order : orderList) {
                    %>
                        <div class="order-card">
                            <div class="order-card-header">
                                <h3>
                                    Mã đơn hàng: #<%= order.getId() %>
                                    <span>(Đặt lúc: <%= dateFormat.format(order.getOrderDate()) %>)</span>
                                </h3>
                                <span class="order-status"><%= order.getStatus() %></span>
                            </div>
                            
                            <div class="order-card-body">
                                <div class="order-shipping-info">
                                    <p><strong>Địa chỉ giao hàng:</strong> <%= order.getShippingAddress() %></p>
                                    <p><strong>Số điện thoại:</strong> <%= order.getShippingPhone() %></p>
                                </div>
                                
                                <table class="order-item-list">
                                    <thead>
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th>Đơn giá</th>
                                            <th>Số lượng</th>
                                            <th style="text-align: right;">Tạm tính</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            // Lặp qua chi tiết của đơn hàng
                                            for (OrderDetail detail : order.getDetails()) {
                                        %>
                                        <tr>
                                            <td class="item-name"><%= detail.getProductName() %></td>
                                            <td class="item-price"><%= formatter.format(detail.getPrice()) %> ₫</td>
                                            <td>x <%= detail.getQuantity() %></td>
                                            <td class="item-total"><%= formatter.format(detail.getPrice() * detail.getQuantity()) %> ₫</td>
                                        </tr>
                                        <%
                                            } // Kết thúc lặp chi tiết
                                        %>
                                    </tbody>
                                </table>
                            </div>
                            
                            <div class="order-card-footer">
                                <span><strong>Tổng cộng:</strong> <%= formatter.format(order.getTotalMoney()) %> ₫</span>
                            </div>
                        </div>
                    <%
                            } // Kết thúc lặp đơn hàng
                        } // Kết thúc else
                    %>
                </div>
                
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>