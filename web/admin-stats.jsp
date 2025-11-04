<%@page import="java.text.DecimalFormat"%>
<%-- === THÊM DÒNG IMPORT BỊ THIẾU NÀY === --%>
<%@page import="Models.Account"%>
<%-- =================================== --%>
<%@page import="Models.OrderDetail"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê Doanh thu</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .stats-container { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }
        .stats-card { border: 1px solid #eee; border-radius: 8px; padding: 25px; background-color: #fcfcfc; }
        .stats-card h2 { margin-top: 0; border-bottom: 1px solid #eee; padding-bottom: 15px; font-size: 20px; }
        .total-revenue-card { grid-column: 1 / -1; text-align: center; background-color: #a40000; color: white; border: none; }
        .total-revenue-card h2 { color: white; border-bottom-color: #c43c3c; }
        .total-revenue-card .revenue-number { font-size: 48px; font-weight: bold; margin: 10px 0; }
        .stats-list { list-style: none; padding: 0; margin: 0; }
        .stats-list li { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f0f0f0; font-size: 15px; }
        .stats-list li:last-child { border-bottom: none; }
        .stats-list .item-name { font-weight: 500; color: #333; }
        .stats-list .item-value { font-weight: bold; color: #a40000; }
    </style>
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        Double totalRevenue = (Double) request.getAttribute("totalRevenue");
        List<OrderDetail> topProducts = (List<OrderDetail>) request.getAttribute("topProducts");
        List<Account> topCustomers = (List<Account>) request.getAttribute("topCustomers");
        DecimalFormat formatter = new DecimalFormat("###,###,###");
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Thống kê Doanh thu</h1>
        <p style="color: #555;">Dữ liệu được tổng hợp từ các đơn hàng có trạng thái "Giao hàng thành công".</p>

        <div class="stats-container">
            
            <div class="stats-card total-revenue-card">
                <h2>Tổng Doanh Thu</h2>
                <div class="revenue-number">
                    <%= formatter.format(totalRevenue != null ? totalRevenue : 0) %> ₫
                </div>
            </div>
            
            <div class="stats-card">
                <h2>Top 10 Sản phẩm Bán chạy (Theo số lượng)</h2>
                <ol class="stats-list" style="list-style-type: decimal; padding-left: 20px;">
                    <% if (topProducts == null || topProducts.isEmpty()) { %>
                        <li>Chưa có dữ liệu.</li>
                    <% } else {
                        for (OrderDetail item : topProducts) {
                    %>
                    <li>
                        <span class="item-name"><%= item.getProductName() %></span>
                        <span class="item-value">Đã bán: <%= item.getQuantity() %></span>
                    </li>
                    <% }} %> 
                </ol>
            </div>
            
            <div class="stats-card">
                <h2>Top 5 Khách hàng (Theo tổng chi)</h2>
                <ol class="stats-list" style="list-style-type: decimal; padding-left: 20px;">
                    <% if (topCustomers == null || topCustomers.isEmpty()) { %>
                        <li>Chưa có dữ liệu.</li>
                    <% } else {
                      
                        for (Account acc : topCustomers) { 
                            double totalSpent = Double.parseDouble(acc.getAddress());
                    %>
                    <li>
                        <span class="item-name"><%= acc.getFullname() %> (<%= acc.getUsername() %>)</span>
                        <span class="item-value"><%= formatter.format(totalSpent) %> ₫</span>
                    </li>
                    <% }} %>
                </ol>
            </div>
            
        </div>
    </main>

    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>