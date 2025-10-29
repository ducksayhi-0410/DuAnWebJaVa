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
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f4f4f4;
            color: #333;
            margin: 0;
            padding: 20px;
        }
        .bill-box {
            max-width: 800px;
            margin: auto;
            padding: 30px;
            border: 1px solid #eee;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
            background-color: #fff;
        }
        .bill-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            border-bottom: 2px solid #eee;
            padding-bottom: 20px;
        }
        .bill-header .logo { font-size: 28px; font-weight: bold; color: #a40000; }
        .bill-header .bill-details { text-align: right; }
        .bill-info {
            display: flex;
            justify-content: space-between;
            margin-top: 30px;
        }
        .bill-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 30px;
        }
        .bill-table thead { background-color: #f9f9f9; }
        .bill-table th, .bill-table td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        .bill-table th:last-child, .bill-table td:last-child { text-align: right; }
        .bill-total {
            margin-top: 20px;
            text-align: right;
            font-size: 18px;
            font-weight: bold;
        }
        .bill-total strong { color: #a40000; }
        .print-button {
            display: inline-block;
            padding: 12px 25px;
            background-color: #a40000;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 30px;
            border: none;
            font-family: 'Roboto', sans-serif;
        }
        .bill-actions { text-align: center; }
        
        @media print {
            body { background-color: #fff; padding: 0; }
            .bill-box { box-shadow: none; border: none; margin: 0; max-width: 100%; }
            .bill-actions { display: none; }
        }
    </style>
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