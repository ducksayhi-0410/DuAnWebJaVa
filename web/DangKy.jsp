<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - Trang Web Bán Rượu</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { background-color: #f5f5f5; }
        .login-container {
            width: 100%; max-width: 450px; margin: 80px auto;
            background: #fff; padding: 30px; border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    
    <div class="login-container">
        <div class="logo" style="text-align: center; margin-bottom: 20px;">
            <a href="products">
                <img src="https://placehold.co/150x50/e8e8e8/333?text=LOGO" alt="Logo">
            </a>
        </div>
        
        <h2 style="text-align: center; margin-bottom: 25px;">Đăng ký tài khoản</h2>
        
        <form action="dangky" method="post">
            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
                <p style="color: red; text-align: center; margin-bottom: 15px;"><%= error %></p>
            <%
                }
            %>

            <div class="form-group">
                <label for="username">Tên đăng nhập</label>
                <input type="text" id="username" name="username" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="form-group">
                <label for="fullname">Họ và Tên</label>
                <input type="text" id="fullname" name="fullname" required>
            </div>
            
            <div class="form-group">
                <label for="phone">Số điện thoại</label>
                <input type="tel" id="phone" name="phone" required>
            </div>

            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="re_password">Nhập lại Mật khẩu</label>
                <input type="password" id="re_password" name="re_password" required>
            </div>
            <button type="submit" class="btn-login">Đăng ký</button>
            
            <p class="form-footer">Đã có tài khoản? <a href="DangNhap.jsp">Đăng nhập ngay</a></p>
        </form>
    </div>

</body>
</html>