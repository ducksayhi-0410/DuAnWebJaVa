<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Trang Web Bán Rượu</title>
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
        <div class="logo" >
            <a href="products">
                <img src="img/logo.png" alt="Logo"/>
            </a>
        </div>
        
        <h2 style="text-align: center; margin-bottom: 25px;">Đăng nhập tài khoản</h2>
        
        <form action="dangnhap" method="post">
            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null) {
            %>
                <p style="color: red; text-align: center; margin-bottom: 15px;"><%= errorMessage %></p>
            <%
                }
            %>
            <div class="form-group">
                <label for="email">Email hoặc Tên đăng nhập</label>
                <input type="text" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn-login">Đăng nhập</button>
            
            <p class="form-footer">Chưa có tài khoản? <a href="DangKy.jsp">Đăng ký ngay</a></p>
        </form>
    </div>

</body>
</html>