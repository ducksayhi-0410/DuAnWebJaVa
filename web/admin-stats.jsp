<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.Account"%>
<%@page import="Models.OrderDetail"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%> <%-- Thêm import Map --%>
<%@page import="java.util.ArrayList"%> <%-- Thêm import List/ArrayList --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê Doanh thu</title>
    <link rel="stylesheet" href="css/style.css">
       <link rel="stylesheet" href="css/order-style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
  
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // Lấy dữ liệu từ Servlet
        Double totalRevenue = (Double) request.getAttribute("totalRevenue");
        List<OrderDetail> topProducts = (List<OrderDetail>) request.getAttribute("topProducts");
        List<Account> topCustomers = (List<Account>) request.getAttribute("topCustomers");
        
        // (MỚI) Lấy dữ liệu biểu đồ
        Map<String, Double> revenueChartData = (Map<String, Double>) request.getAttribute("revenueChartData");
        Map<String, Double> registrationChartData = (Map<String, Double>) request.getAttribute("registrationChartData");

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        // (MỚI) Chuyển đổi Map Java sang List JavaScript để dùng cho Chart.js
        List<String> revenueLabels = new ArrayList<>();
        List<Double> revenueValues = new ArrayList<>();
        if (revenueChartData != null) {
            for (Map.Entry<String, Double> entry : revenueChartData.entrySet()) {
                revenueLabels.add("\"" + entry.getKey() + "\""); // Thêm dấu " "
                revenueValues.add(entry.getValue());
            }
        }
        
        List<String> regLabels = new ArrayList<>();
        List<Double> regValues = new ArrayList<>();
        if (registrationChartData != null) {
            for (Map.Entry<String, Double> entry : registrationChartData.entrySet()) {
                regLabels.add("\"" + entry.getKey() + "\"");
                regValues.add(entry.getValue());
            }
        }
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Dashboard Thống kê</h1>
        <p style="color: #555;">Dữ liệu được tổng hợp từ các đơn hàng có trạng thái "Giao hàng thành công".</p>

        <div class="stats-container">
            
            <div class="stats-card total-revenue-card full-width">
                <h2>Tổng Doanh Thu (Đã Giao)</h2>
                <div class="revenue-number">
                    <%= formatter.format(totalRevenue != null ? totalRevenue : 0) %> ₫
                </div>
            </div>
            
            <div class="stats-card full-width">
                <h2>Doanh thu hàng tháng</h2>
                <canvas id="revenueChart"></canvas>
            </div>
            
            <div class="stats-card">
                <h2>Đăng ký mới hàng tháng</h2>
                <canvas id="registrationChart"></canvas>
                <% if (registrationChartData == null || registrationChartData.isEmpty()) { %>
                    <p style="text-align:center; color: red;">(Không tìm thấy dữ liệu. Bạn đã chạy lệnh ALTER TABLE để thêm cột 'registration_date' chưa?)</p>
                <% } %>
            </div>
            
            <div class="stats-card">
                <h2>Top 10 Sản phẩm Bán chạy</h2>
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
            
            <div class="stats-card full-width">
                <h2>Top 5 Khách hàng (Theo tổng chi)</h2>
                <ol class="stats-list" style="list-style-type: decimal; padding-left: 20px;">
                    <% if (topCustomers == null || topCustomers.isEmpty()) { %>
                        <li>Chưa có dữ liệu.</li>
                    <% } else {
                        // Sửa lỗi: đổi tên biến 'acc' thành 'customerAcc'
                        for (Account customerAcc : topCustomers) { 
                            double totalSpent = Double.parseDouble(customerAcc.getAddress());
                    %>
                    <li>
                        <span class="item-name"><%= customerAcc.getFullname() %> (<%= customerAcc.getUsername() %>)</span>
                        <span class="item-value"><%= formatter.format(totalSpent) %> ₫</span>
                    </li>
                    <% }} %>
                </ol>
            </div>
            
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            
            // 1. Dữ liệu Biểu đồ Doanh thu (Line)
            const revenueCtx = document.getElementById('revenueChart');
            if (revenueCtx) {
                new Chart(revenueCtx, {
                    type: 'line',
                    data: {
                        labels: [<%= String.join(",", revenueLabels) %>],
                        datasets: [{
                            label: 'Doanh thu (VND)',
                            data: [<%= String.join(",", revenueValues.toString().replace("[","").replace("]","")) %>],
                            fill: true,
                            borderColor: '#a40000',
                            backgroundColor: 'rgba(164, 0, 0, 0.1)',
                            tension: 0.1
                        }]
                    },
                    options: {
                        scales: {
                            y: { beginAtZero: true }
                        }
                    }
                });
            }

            // 2. Dữ liệu Biểu đồ Đăng ký (Bar)
            const regCtx = document.getElementById('registrationChart');
            if (regCtx) {
                new Chart(regCtx, {
                    type: 'bar',
                    data: {
                        labels: [<%= String.join(",", regLabels) %>],
                        datasets: [{
                            label: 'Số lượng đăng ký',
                            data: [<%= String.join(",", regValues.toString().replace("[","").replace("]","")) %>],
                            backgroundColor: '#0275d8'
                        }]
                    },
                    options: {
                        scales: {
                            y: { beginAtZero: true }
                        }
                    }
                });
            }
        });
    </script>
    
</body>
</html>