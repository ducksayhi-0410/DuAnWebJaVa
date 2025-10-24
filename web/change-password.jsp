<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu</title>
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
        request.setAttribute("activePage", "password");
        
        // Lấy thông báo lỗi/thành công (nếu có) từ Servlet
        String error = (String) request.getAttribute("error");
        String success = (String) request.getAttribute("success");
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="profile">Tài khoản</a> / <span>Đổi mật khẩu</span>
        </div>
        
        <div class="account-container">
            <%@ include file="WEB-INF/account-nav.jspf" %>
            
            <section class="account-content">
                <h1>Đổi mật khẩu</h1>
                
                <form action="change-password" method="POST">
                    
                    <% if (error != null) { %>
                        <div class="cart-notification error"><%= error %></div>
                    <% } %>
                    <% if (success != null) { %>
                        <div class="cart-notification success"><%= success %></div>
                    <% } %>

                    <div class="form-group">
                        <label for="old_password">Mật khẩu cũ</label>
                        <input type="password" id="old_password" name="old_password" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="new_password">Mật khẩu mới</label>
                        <input type="password" id="new_password" name="new_password" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="re_new_password">Xác nhận mật khẩu mới</label>
                        <input type="password" id="re_new_password" name="re_new_password" required>
                    </div>
                    
                    <button type="submit" class="btn-submit">Đổi mật khẩu</button>
                </form>
                
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>