<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt hàng thành công</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
    <style>
        .success-container {
            text-align: center;
            padding: 50px 20px; margin: 30px auto;
            max-width: 600px; background: #f9f9f9; border: 1px solid #eee; border-radius: 8px;
        }
        .success-container i { font-size: 50px; color: #28a745; }
        .success-container h1 { color: #333; margin-top: 20px; }
        .success-container p { font-size: 16px; color: #555; line-height: 1.6; }
        .success-actions { margin-top: 30px; display: flex; gap: 15px; justify-content: center; }
    </style>
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <main class="container">
        <div class="success-container">
            <i class="fas fa-check-circle"></i>
            <h1>Cảm ơn bạn đã mua hàng!</h1>
            
            <% Integer orderId = (Integer) request.getAttribute("orderId"); %>
            <p>Đơn hàng #<%= (orderId != null) ? orderId : "..." %> của bạn đã được tiếp nhận và đang chờ xử lý.
            Chúng tôi sẽ liên hệ với bạn qua số điện thoại đã cung cấp để xác nhận.</p>
            
            <div class="success-actions">
                <a href="products" class="btn-detail">Quay về trang chủ</a>
                
                <a href="export-bill?orderId=<%= (orderId != null) ? orderId : "" %>" 
                   class="btn-checkout">Xuất biên lai (PDF)</a>
            </div>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
</body>
</html>