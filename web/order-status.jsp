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
    <title>Trạng thái Đơn hàng</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* (CSS dùng lại từ my-orders.jsp và employee-orders.jsp) */
        .order-list { display: flex; flex-direction: column; gap: 20px; }
        .order-card { border: 1px solid #eee; border-radius: 8px; background-color: #fff; overflow: hidden; }
        .order-card-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: #f9f9f9; border-bottom: 1px solid #eee; }
        .order-card-header h3 { margin: 0; font-size: 16px; color: #a40000; }
        .order-card-header span { font-size: 14px; color: #555; font-weight: 400; }
        .order-status { font-weight: bold; font-size: 14px; padding: 5px 10px; border-radius: 5px; }
        .order-status[data-status="Đang chuẩn bị hàng"] { background-color: #ffc107; color: #333; }
        .order-status[data-status="Đang giao hàng"] { background-color: #0275d8; color: white; }
        
        .order-card-body { padding: 20px; }
        .order-item-list { width: 100%; border-collapse: collapse; }
        .order-item-list th, .order-item-list td { padding: 10px 0; border-bottom: 1px solid #f0f0f0; text-align: left; font-size: 14px; }
        .order-item-list th { font-weight: 500; color: #666; }
        .order-item-list tr:last-child td { border-bottom: none; }
        .order-item-list .item-total { font-weight: bold; color: #a40000; text-align: right; }
        
        .order-card-footer { display: flex; justify-content: flex-end; align-items: center; padding: 15px 20px; background-color: #f9f9f9; border-top: 1px solid #eee; }
        .order-card-footer span { font-size: 18px; font-weight: bold; color: #a40000; }
        
        /* CSS cho thông báo trống */
        .empty-status-message {
            border: 1px dashed #ccc;
            padding: 30px;
            text-align: center;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .empty-status-message h3 { margin-top: 0; }
    </style>
</head>
<body>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        List<Order> processingOrders = (List<Order>) request.getAttribute("processingOrders");
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="profile">Tài khoản</a> / <span>Trạng thái Đơn hàng</span>
        </div>
        
        <div class="account-container">
            <%@ include file="WEB-INF/account-nav.jspf" %>
            
            <section class="account-content">
                <h1>Trạng thái Đơn hàng</h1>
                
                <div class="order-list">
                  
                    <%-- KIỂM TRA ĐIỀU KIỆN MỚI THEO YÊU CẦU --%>
                    <% if (processingOrders == null || processingOrders.isEmpty()) { %>
                        <div class="empty-status-message">
                            <h3>Bạn hiện không có đơn hàng nào đang được xử lý.</h3>
                            <p style="margin-bottom: 20px; color: #555;">Tất cả đơn hàng đã hoàn thành có thể được xem trong Lịch sử mua hàng.</p>
                            <a href="products" class="btn-detail" style="background-color:#a40000; color:white;">Bắt đầu mua sắm</a>
                        </div>
                    <%
                        } else {
                            // Lặp qua các đơn đang xử lý
                            for (Order order : processingOrders) {
                    %>
                        <div class="order-card">
                            <div class="order-card-header">
                                <h3>
                                    Mã đơn: #<%= order.getId() %>
                                    <span>(<%= dateFormat.format(order.getOrderDate()) %>)</span>
                                </h3>
                                <span class="order-status" data-status="<%= order.getStatus() %>">
                                    <%= order.getStatus() %>
                                </span>
                            </div>
                            
                            <div class="order-card-body">
                                <table class="order-item-list">
                                    <thead>
                                        <tr>
                                            <th>Sản phẩm</th> <th>Đơn giá</th> <th>SL</th> <th style="text-align: right;">Tạm tính</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (OrderDetail detail : order.getDetails()) { %>
                                        <tr>
                                            <td><%= detail.getProductName() %></td>
                                            <td><%= formatter.format(detail.getPrice()) %> ₫</td>
                                            <td>x <%= detail.getQuantity() %></td>
                                            <td class="item-total"><%= formatter.format(detail.getPrice() * detail.getQuantity()) %> ₫</td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            
                            <div class="order-card-footer">
                                <span><strong>Tổng cộng:</strong> <%= formatter.format(order.getTotalMoney()) %> ₫</span>
                            </div>
                        </div>
                    <%
                            } // Kết thúc lặp
                        } // Kết thúc else
                    %>
                </div>
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>