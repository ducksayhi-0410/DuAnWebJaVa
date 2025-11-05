<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.OrderDetail"%>
<%@page import="Models.Order"%>
<%@page import="Models.Account"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Biên lai</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
     <link rel="stylesheet" href="css/bill-style.css">
</head>
<body>

    <%
        Order order = (Order) request.getAttribute("order");
        Account acc = (Account) session.getAttribute("acc");
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        
        if (order == null || acc == null) {
            out.println("Không tìm thấy biên lai hoặc bạn chưa đăng nhập.");
            return;
        }
    %>

    <div class="bill-box">
        <div class="bill-header">
            <div class="logo">
                RƯỢU VANG SỦI
            </div>
            <div class="bill-details">
                <strong>Biên lai #<%= order.getId() %></strong><br>
                Ngày đặt: <%= dateFormat.format(order.getOrderDate()) %><br>
                Trạng thái: <%= order.getStatus() %>
            </div>
        </div>

        <div class="bill-info">
            <div>
                <strong>Thông tin khách hàng:</strong><br>
                <%= acc.getFullname() %><br>
                <%= acc.getEmail() %><br>
                <%= order.getShippingPhone() %>
            </div>
            <div>
                <strong>Địa chỉ giao hàng:</strong><br>
                <%= order.getShippingAddress() %>
            </div>
        </div>

        <table class="bill-table">
            <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th>Đơn giá</th>
                    <th>Số lượng</th>
                    <th>Thành tiền</th>
                </tr>
            </thead>
            <tbody>
                <% for (OrderDetail detail : order.getDetails()) { %>
                <tr>
                    <td><%= detail.getProductName() %></td>
                    <td><%= formatter.format(detail.getPrice()) %> ₫</td>
                    <td><%= detail.getQuantity() %></td>
                    <td style="text-align: right;"><%= formatter.format(detail.getPrice() * detail.getQuantity()) %> ₫</td>
                </tr>
                <% } %>
            </tbody>
        </table>

        <div class="bill-total">
            Tổng cộng: <strong><%= formatter.format(order.getTotalMoney()) %> ₫</strong>
        </div>

        <div class="bill-actions">
            <button onclick="window.print()" class="print-button">
                In biên lai (hoặc Lưu PDF)
            </button>
        </div>
    </div>
</body>
</html>