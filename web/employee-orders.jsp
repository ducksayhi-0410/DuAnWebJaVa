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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* (CSS này giống hệt CSS trong my-orders.jsp) */
        .order-list { display: flex; flex-direction: column; gap: 20px; }
        .order-card { border: 1px solid #eee; border-radius: 8px; background-color: #fff; overflow: hidden; }
        .order-card-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: #f9f9f9; border-bottom: 1px solid #eee; flex-wrap: wrap; gap: 10px; }
        .order-card-header h3 { margin: 0; font-size: 16px; color: #a40000; }
        .order-card-header h3 span { font-size: 14px; color: #555; font-weight: 400; }
        .order-status { font-weight: bold; font-size: 14px; padding: 5px 10px; border-radius: 5px; }
        .order-status[data-status="Đang chuẩn bị hàng"] { background-color: #ffc107; color: #333; }
        .order-status[data-status="Đang giao hàng"] { background-color: #0275d8; color: white; }
        .order-status[data-status="Giao hàng thành công"] { background-color: #5cb85c; color: white; }
        
        .order-card-body { padding: 20px; display: flex; flex-wrap: wrap; gap: 20px; }
        .order-details-col { flex: 2; min-width: 300px; }
        .order-info-col { flex: 1; min-width: 250px; }
        .order-info-col p { margin: 5px 0; font-size: 14px; line-height: 1.6; }
        .order-info-col p strong { color: #333; }
        
        .order-item-list { width: 100%; border-collapse: collapse; }
        .order-item-list th, .order-item-list td { padding: 10px 0; border-bottom: 1px solid #f0f0f0; text-align: left; font-size: 14px; }
        .order-item-list th { font-weight: 500; color: #666; }
        .order-item-list tr:last-child td { border-bottom: none; }
        .order-item-list .item-total { font-weight: bold; color: #a40000; text-align: right; }
        
        .order-card-footer { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: #f9f9f9; border-top: 1px solid #eee; }
        .order-card-footer .total-display span { font-size: 18px; font-weight: bold; color: #a40000; }
        
        .status-update-form { display: flex; gap: 10px; align-items: center; }
        .status-update-form select { padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; }
        .status-update-form .btn-submit-status {
            padding: 8px 15px; font-size: 14px; background-color: #a40000; color: white;
            border: 1px solid #a40000; border-radius: 4px; font-weight: bold; cursor: pointer;
        }
    </style>
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        List<Order> allOrders = (List<Order>) request.getAttribute("allOrders");
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        String[] statuses = {"Đang chuẩn bị hàng", "Đang giao hàng", "Giao hàng thành công"};
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