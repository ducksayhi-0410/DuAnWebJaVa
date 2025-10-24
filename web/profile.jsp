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
</head>
<body>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // Giả sử Servlet đã kiểm tra đăng nhập và đặt 'acc' vào session
    
        
        // Đặt 'activePage' cho menu (bạn nên làm việc này ở Servlet)
        request.setAttribute("activePage", "profile");
        
        // Xử lý giá trị null để hiển thị
        String fullname = (acc.getFullname() == null || acc.getFullname().isEmpty()) ? "<i>Chưa cập nhật</i>" : acc.getFullname();
        String phone = (acc.getPhone() == null || acc.getPhone().isEmpty()) ? "<i>Chưa cập nhật</i>" : acc.getPhone();
        String address = (acc.getAddress() == null || acc.getAddress().isEmpty()) ? "<i>Chưa cập nhật</i>" : acc.getAddress();
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
                </table>
                
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>