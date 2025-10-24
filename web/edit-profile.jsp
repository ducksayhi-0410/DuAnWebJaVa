<%@page import="Models.Account"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa thông tin cá nhân</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
       
        
        // Đặt 'activePage' cho menu (bạn nên làm việc này ở Servlet)
        request.setAttribute("activePage", "edit");
        
        // Lấy thông báo lỗi/thành công (nếu có) từ Servlet
        String error = (String) request.getAttribute("error");
        String success = (String) request.getAttribute("success");
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="profile">Tài khoản</a> / <span>Sửa thông tin</span>
        </div>
        
        <div class="account-container">
            <%@ include file="WEB-INF/account-nav.jspf" %>
            
            <section class="account-content">
                <h1>Sửa thông tin cá nhân</h1>
                
                <form action="edit-profile" method="POST">
                    
                    <% if (error != null) { %>
                        <div class="cart-notification error"><%= error %></div>
                    <% } %>
                    <% if (success != null) { %>
                        <div class="cart-notification success"><%= success %></div>
                    <% } %>
                    
                    <div class="form-group">
                        <label for="username">Tên đăng nhập (Không thể đổi)</label>
                        <input type="text" id="username" name="username" value="<%= acc.getUsername() %>" readonly style="background-color: #f0f0f0;">
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email (Không thể đổi)</label>
                        <input type="email" id="email" name="email" value="<%= acc.getEmail() %>" readonly style="background-color: #f0f0f0;">
                    </div>

                    <div class="form-group">
                        <label for="fullname">Họ và Tên</label>
                        <input type="text" id="fullname" name="fullname" value="<%= (acc.getFullname() != null) ? acc.getFullname() : "" %>" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="phone">Số điện thoại</label>
                        <input type="tel" id="phone" name="phone" value="<%= (acc.getPhone() != null) ? acc.getPhone() : "" %>" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Địa chỉ</label>
                        <input type="text" id="address" name="address" value="<%= (acc.getAddress() != null) ? acc.getAddress() : "" %>">
                    </div>
                    
                    <button type="submit" class="btn-submit">Lưu thay đổi</button>
                </form>
                
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>