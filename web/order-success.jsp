<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt hàng thành công!</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .success-container {
            text-align: center;
            padding: 80px 20px;
            background: #f9f9f9;
            border-radius: 8px;
            margin-top: 30px;
        }
        .success-container i {
            font-size: 60px;
            color: #155724; /* Màu xanh lá */
            margin-bottom: 20px;
        }
        .success-container h1 {
            font-size: 28px;
            color: #155724;
        }
        .success-container p {
            font-size: 16px;
            color: #333;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>

    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <main class="container">
        <div class="success-container">
            <i class="fas fa-check-circle"></i>
            <h1>Đặt hàng thành công!</h1>
            <p>Cảm ơn bạn đã mua hàng. Đơn hàng của bạn đang được xử lý.</p>
            <div>
                <a href="my-orders" class="btn-detail" style="background-color: #a40000; color: white;">Xem lịch sử mua hàng</a>
                <a href="products" class="btn-detail" style="margin-left: 10px;">Tiếp tục mua sắm</a>
            </div>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
</body>
</html>