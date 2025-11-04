<%@page import="java.text.DecimalFormat"%> <%-- Thêm import --%>
<%@page import="Models.Account"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin cá nhân</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
    <style>
       
    </style>
</head>
<body>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        request.setAttribute("activePage", "profile");
        
        // Xử lý giá trị null
        String fullname = (acc.getFullname() == null || acc.getFullname().isEmpty()) ?
            "<i>Chưa cập nhật</i>" : acc.getFullname();
        String phone = (acc.getPhone() == null || acc.getPhone().isEmpty()) ? "<i>Chưa cập nhật</i>" : acc.getPhone();
        String address = (acc.getAddress() == null || acc.getAddress().isEmpty()) ? "<i>Chưa cập nhật</i>" : acc.getAddress();
        
        // === LOGIC MỚI CHO HẠNG THÀNH VIÊN ===
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        // Các mốc chi tiêu (phải khớp với AccountDb.java)
        final double BAC_TIER = 5000000;
        final double VANG_TIER = 20000000;
        final double KIM_CUONG_TIER = 50000000;

        String tier = acc.getCustomerTier();
        double spend = acc.getLifetimeSpend();
        
        String tierName = "Đồng";
        String tierIcon = "fa-leaf";
        String nextTierName = "Bạc";
        double nextTierSpend = BAC_TIER;
        
        if ("kimcuong".equals(tier)) {
            tierName = "Kim Cương";
            tierIcon = "fa-gem";
            nextTierName = null; // Hạng cao nhất
        } else if ("vang".equals(tier)) {
            tierName = "Vàng";
            tierIcon = "fa-medal";
            nextTierName = "Kim Cương";
            nextTierSpend = KIM_CUONG_TIER;
        } else if ("bac".equals(tier)) {
            tierName = "Bạc";
            tierIcon = "fa-shield-alt";
            nextTierName = "Vàng";
            nextTierSpend = VANG_TIER;
        }
        
        double amountNeeded = Math.max(0, nextTierSpend - spend);
        double progressPercent = (nextTierName == null) ? 100 : (spend / nextTierSpend) * 100;
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <span>Tài khoản của tôi</span>
        </div>
        
        <div class="account-container">
            <%@ include file="WEB-INF/account-nav.jspf" %>
            
            <section class="account-content">
                <h1>Thông tin cá nhân</h1>
                
                <table class="profile-info-table">
                    <tr>
                        <td>Tên đăng nhập:</td>
                        <td><%= acc.getUsername() %></td>
                    </tr>
                    <tr>
                        <td>Email:</td>
                        <td><%= acc.getEmail() %></td>
                    </tr>
                    <tr>
                        <td>Họ và Tên:</td>
                        <td><%= fullname %></td>
                    </tr>
                    <tr>
                        <td>Số điện thoại:</td>
                        <td><%= phone %></td>
                    </tr>
                    <tr>
                        <td>Địa chỉ:</td>
                        <td><%= address %></td>
                    </tr>
                    <tr>
                        <td>Vai trò:</td>
                        <td style="text-transform: capitalize;"><%= acc.getRole() %></td>
                    </tr>
                    
                    <tr>
                        <td>Tổng chi tiêu:</td>
                        <td><%= formatter.format(spend) %> ₫</td>
                    </tr>
                    <tr>
                        <td>Hạng thành viên:</td>
                        <td>
                            <div class="tier-card">
                                <div class="tier-name">
                                    <i class="fas <%= tierIcon %>"></i> Hạng <%= tierName %>
                                </div>
                                
                                <% if (nextTierName != null) { %>
                                    <div class="progress-bar">
                                        <div class="progress-bar-inner" style="width: <%= progressPercent %>%;"></div>
                                    </div>
                                    <div class="tier-info">
                                        <span>Chi tiêu thêm <strong><%= formatter.format(amountNeeded) %> ₫</strong> để lên Hạng <%= nextTierName %></span>
                                        <span><%= formatter.format(nextTierSpend) %> ₫</span>
                                    </div>
                                <% } else { %>
                                    <p style="font-size: 14px; color: #555; margin-top: 10px;">
                                        Bạn đã đạt hạng cao nhất. Cảm ơn bạn đã đồng hành!
                                    </p>
                                <% } %>
                                
                                <a href="membership-perks" style="font-size: 13px; margin-top: 10px; display: inline-block;">
                                    Xem tất cả đặc quyền <i class="fas fa-arrow-right fa-xs"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                    </table>
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>