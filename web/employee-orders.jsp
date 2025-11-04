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
    </head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        List<Order> allOrders = (List<Order>) request.getAttribute("allOrders");
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        String[] statuses = {"Đang xác nhận", "Đang chuẩn bị hàng", "Đang giao hàng", "Giao hàng thành công"};
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Quản lý Đơn hàng (<%= allOrders != null ? allOrders.size() : 0 %>)</h1>

        <div class="order-list">
            <% if (allOrders != null && !allOrders.isEmpty()) {
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
                    </div>
                
                <div class="order-card-footer">
                    <div class="total-display">
                        <span><strong>Tổng cộng:</strong> <%= formatter.format(order.getTotalMoney()) %> ₫</span>
                    </div>
                    
                    <form action="employee-orders" method="POST" class="status-update-form">
                        <input type="hidden" name="orderId" value="<%= order.getId() %>">
                        
                        <input type="hidden" name="username" value="<%= order.getUsername() %>">
                        <input type="hidden" name="totalMoney" value="<%= order.getTotalMoney() %>">
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