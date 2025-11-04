<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặc quyền Thành viên</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
   
</head>
<body>
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        // Servlet đã đặt activePage = "perks"
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="profile">Tài khoản</a> / <span>Đặc quyền Thành viên</span>
        </div>
        
        <div class="account-container">
            <%@ include file="WEB-INF/account-nav.jspf" %>
            
            <section class="account-content">
                <h1>Đặc quyền Thành viên</h1>
                <p style="color: #555; font-size: 16px;">
                    Chúng tôi trân trọng sự đồng hành của bạn. Khám phá các đặc quyền khi bạn thăng hạng!
                </p>
                
                <div class="perks-grid">
                    <div class="perk-card tier-dong">
                        <i class="fas fa-leaf perk-icon"></i>
                        <h3>Hạng Đồng</h3>
                        <div class="perk-goal">Chi tiêu từ 0đ</div>
                        <ul>
                            <li><i class="fas fa-check"></i> <span>Tích điểm cơ bản</span></li>
                            <li><i class="fas fa-check"></i> <span>Nhận thông tin khuyến mãi</span></li>
                        </ul>
                        <div class="perk-footer">Hạng khởi điểm</div>
                    </div>
                    
                    <div class="perk-card tier-bac">
                        <i class="fas fa-shield-alt perk-icon"></i>
                        <h3>Hạng Bạc</h3>
                        <div class="perk-goal">Chi tiêu 5.000.000đ</div>
                        <ul>
                            <li><i class="fas fa-check"></i> <span>Toàn bộ quyền lợi Hạng Đồng</span></li>
                            <li><i class="fas fa-check"></i> <span><strong>Chiết khấu tự động 2%</strong></span></li>
                            <li><i class="fas fa-check"></i> <span>Quà tặng sinh nhật</span></li>
                        </ul>
                        <div class="perk-footer">Lên hạng từ Đồng</div>
                    </div>
                    
                    <div class="perk-card tier-vang">
                        <i class="fas fa-medal perk-icon"></i>
                        <h3>Hạng Vàng</h3>
                        <div class="perk-goal">Chi tiêu 20.000.000đ</div>
                        <ul>
                            <li><i class="fas fa-check"></i> <span>Toàn bộ quyền lợi Hạng Bạc</span></li>
                            <li><i class="fas fa-check"></i> <span><strong>Chiết khấu tự động 5%</strong></span></li>
                            <li><i class="fas fa-check"></i> <span>Miễn phí vận chuyển</span></li>
                            <li><i class="fas fa-check"></i> <span>Voucher độc quyền hàng tháng</span></li>
                        </ul>
                        <div class="perk-footer">Lên hạng từ Bạc</div>
                    </div>
                    
                    <div class="perk-card tier-kimcuong">
                        <i class="fas fa-gem perk-icon"></i>
                        <h3>Hạng Kim Cương</h3>
                        <div class="perk-goal">Chi tiêu 50.000.000đ</div>
                        <ul>
                            <li><i class="fas fa-check"></i> <span>Toàn bộ quyền lợi Hạng Vàng</span></li>
                            <li><i class="fas fa-check"></i> <span><strong>Chiết khấu tự động 10%</strong></span></li>
                            <li><i class="fas fa-check"></i> <span>Ưu tiên giao hàng nhanh</span></li>
                            <li><i class="fas fa-check"></i> <span>Tham dự sự kiện thử rượu</span></li>
                        </ul>
                        <div class="perk-footer">Hạng cao nhất</div>
                    </div>
                </div>
                
            </section>
        </div>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>