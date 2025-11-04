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
    <title>Quản lý Đơn hàng</title>
    
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/order-style.css">
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        List<Order> allOrders = (List<Order>) request.getAttribute("allOrders");
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        
        // === CẬP NHẬT DANH SÁCH TRẠNG THÁI ===
        String[] statuses = {
            "Đang xác nhận",
            "Đang chuẩn bị hàng", 
            "Đang giao hàng", 
            "Giao hàng thành công"
        };
        // === KẾT THÚC CẬP NHẬT ===
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Quản lý Đơn hàng (<%= allOrders != null ? allOrders.size() : 0 %>)</h1>

        <div class="order-list">
            <% if (allOrders == null || allOrders.isEmpty()) { %>
                <p>Chưa có đơn hàng nào trong hệ thống.</p>
            <% } else {
                for (Order order : allOrders) {
            %>
            <div class="order-card">
                <div class="order-card-header">
                    <h3>
                        Mã đơn: #<%= order.getId() %>
                        <span>(Khách: <%= order.getUsername() %> - <%= dateFormat.format(order.getOrderDate()) %>)</span>
                    </h3>
                    <span class="order-status" data-status="<%= order.getStatus() %>">
                        <%= order.getStatus() %>
                    </span>
                </div>
                
                <div class="order-card-body">
                    <div class="order-details-col">
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
                    
                    <div class="order-info-col">
                        <p><strong>Khách hàng:</strong> <%= order.getUsername() %></p>
                        <p><strong>Số điện thoại:</strong> <%= order.getShippingPhone() %></p>
                        <p><strong>Địa chỉ:</strong> <%= order.getShippingAddress() %></p>
                    </div>
                </div>
                
                <div class="order-card-footer">
                    <div class="total-display">
                        <span><strong>Tổng cộng:</strong> <%= formatter.format(order.getTotalMoney()) %> ₫</span>
                    </div>
                    
                    <form action="employee-orders" method="POST" class="status-update-form">
                        <input type="hidden" name="orderId" value="<%= order.getId() %>">
                        <select name="status">
                            <% for (String s : statuses) {
                                boolean isSelected = s.equals(order.getStatus());
                            %>
                            <option value="<%= s %>" <%= isSelected ? "selected" : "" %>>
                                <%= s %>
                            </option>
                            <% } %>
                        </select>
                        <button type="submit" class="btn-submit-status">Cập nhật</button>
                    </form>
                </div>
            </div>
            <%
                } // Kết thúc lặp
            } // Kết thúc else
            %>
        </div>
        
    </main>

    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>