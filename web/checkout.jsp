<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.Cart"%>
<%@page import="Models.Account"%>
<%@page import="Models.Item"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
    <style>
        /* (Giữ nguyên CSS cho Form Voucher) */
        .voucher-form { display: flex; gap: 10px; margin-top: 20px; }
        .voucher-form input { flex-grow: 1; padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
        .voucher-form button { padding: 10px 15px; font-weight: bold; border: 1px solid #a40000; background-color: #a40000; color: white; border-radius: 4px; cursor: pointer; }
        .voucher-form button:disabled { background-color: #ccc; border-color: #ccc; cursor: not-allowed; }
        #voucher-message { font-size: 13px; margin-top: 10px; }
        #voucher-message.success { color: green; }
        #voucher-message.error { color: red; }
    </style>
</head>
<body>

    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        session.removeAttribute("appliedVoucher");
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart");
            return;
        }
        
        List<Item> items = cart.getItems();
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String checkoutError = (String) request.getAttribute("checkoutError");
        
        String defaultPhone = (acc.getPhone() != null) ? acc.getPhone() : "";
        String defaultAddress = (acc.getAddress() != null) ? acc.getAddress() : "";
        
        double subtotal = cart.getTotalMoney();
        String tier = acc.getCustomerTier();
        String tierName = "Đồng";
        double memberDiscountPercentage = 0;
        
        if ("kimcuong".equals(tier)) {
            tierName = "Kim Cương";
            memberDiscountPercentage = 0.1; // 10%
        } else if ("vang".equals(tier)) {
            tierName = "Vàng";
            memberDiscountPercentage = 0.05; // 5%
        } else if ("bac".equals(tier)) {
            tierName = "Bạc";
            memberDiscountPercentage = 0.02; // 2%
        }
        
        double memberDiscountAmount = subtotal * memberDiscountPercentage;
        double priceAfterMemberDiscount = subtotal - memberDiscountAmount;
        
        // === SỬA LỖI ÂM TIỀN (HIỂN THỊ BAN ĐẦU) ===
        if (priceAfterMemberDiscount < 0) {
            priceAfterMemberDiscount = 0;
        }
        // ======================================
    %>
    
    <main class="container">
        <div class="breadcrumbs" style="margin-top: 20px;">
            <a href="products">Trang chủ</a> / <a href="cart">Giỏ hàng</a> / <span>Thanh toán</span>
        </div>
        
        <h1>Chi tiết thanh toán</h1>
        
        <form action="place-order" method="POST">
            <div class="checkout-container">
 
                <div class="customer-details">
                    <h2>Thông tin khách hàng (Hạng: <%= tierName %>)</h2>
                    
                    <% if (checkoutError != null) { %>
                         <div class="cart-notification error"><%= checkoutError %></div>
                    <% } %>
                    
                    <div class="form-group">
                        <label for="fullname">Họ và Tên</label>
                        <input type="text" id="fullname" name="fullname" value="<%= acc.getFullname() %>" readonly>
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<%= acc.getEmail() %>" readonly>
                    </div>
                    <div class="form-group">
                        <label for="phone">Số điện thoại giao hàng</label>
                        <input type="tel" id="phone" name="phone" value="<%= defaultPhone %>" required>
                    </div>
                    <div class="form-group">
                        <label for="address">Địa chỉ giao hàng</label>
                        <input type="text" id="address" name="address" value="<%= defaultAddress %>" required>
                    </div>
                    
                    <h3 style="margin-top: 30px;">Mã giảm giá</h3>
                    <div class="voucher-form">
                        <input type="text" id="voucher-code" placeholder="Nhập mã giảm giá">
                        <button type="button" id="apply-voucher-btn">Áp dụng</button>
                    </div>
                    <div id="voucher-message"></div>
                </div>

                <div class="order-summary">
                    <h2>Đơn hàng của bạn</h2>
                    <table class="order-table">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Tạm tính</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Item item : items) { %>
                            <tr>
                                <td><%= item.getProduct().getName() %> <strong>× <%= item.getQuantity() %></strong></td>
                                <td><%= formatter.format(item.getProduct().getPrice() * item.getQuantity()) %> ₫</td>
                            </tr>
                            <% } %>
                            
                            <tr class="total-row" style="font-size: 16px; font-weight: normal;">
                                <td><strong>Tạm tính</strong></td>
                                <td id="subtotal"><%= formatter.format(subtotal) %> ₫</td>
                            </tr>
                            
                            <% if (memberDiscountAmount > 0) { %>
                            <tr class="total-row" style="font-size: 16px; font-weight: normal; color: #a40000;">
                                <td><strong>Chiết khấu (Hạng <%= tierName %>)</strong></td>
                                <td id="member-discount">- <%= formatter.format(memberDiscountAmount) %> ₫</td>
                            </tr>
                            <% } %>
                            
                            <tr class="total-row" id="voucher-row" style="display:none; font-size: 16px; font-weight: normal; color: #a40000;">
                                <td><strong>Giảm giá Voucher</strong></td>
                                <td id="voucher-discount"></td>
                            </tr>
                            
                            <tr class="total-row">
                                <td><strong>Tổng cộng</strong></td>
                                <td id="final-total"><strong><%= formatter.format(priceAfterMemberDiscount) %> ₫</strong></td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <h3 style="margin-top: 20px;">Phương thức thanh toán</h3>
                    <p>Thanh toán khi nhận hàng (COD)</p>
                    
                    <button type="submit" class="btn-checkout" style="margin-top: 20px;">
                        XÁC NHẬN ĐẶT HÀNG
                    </button>
                </div>
            </div>
        </form>
    </main>
    
    <%@ include file="WEB-INF/main-footer.jspf" %>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const applyBtn = document.getElementById('apply-voucher-btn');
            const voucherInput = document.getElementById('voucher-code');
            const messageEl = document.getElementById('voucher-message');
            
            let currentTotal = <%= priceAfterMemberDiscount %>;
            const formatter = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });

            applyBtn.addEventListener('click', function() {
                const code = voucherInput.value;
                if (!code) {
                    messageEl.textContent = 'Vui lòng nhập mã giảm giá.';
                    messageEl.className = 'error';
                    return;
                }
                
                fetch('apply-voucher', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded', },
                    body: 'code=' + encodeURIComponent(code)
                })
                .then(response => response.json())
                .then(data => {
                    messageEl.textContent = data.message;
                    if (data.success) {
                        messageEl.className = 'success';
                        applyBtn.disabled = true;
                        voucherInput.disabled = true;
                        
                        const voucherDiscount = data.discountAmount;
                        let finalTotal = currentTotal - voucherDiscount;
                        
                        // === SỬA LỖI ÂM TIỀN (JAVASCRIPT) ===
                        if (finalTotal < 0) {
                            finalTotal = 0;
                        }
                        // ===================================
                        
                        document.getElementById('voucher-row').style.display = 'table-row';
                        document.getElementById('voucher-discount').textContent = data.discountAmountFormatted;
                        document.getElementById('final-total').innerHTML = '<strong>' + formatter.format(finalTotal) + '</strong>';
                        
                    } else {
                        messageEl.className = 'error';
                    }
                })
                .catch(err => {
                    messageEl.textContent = 'Lỗi kết nối máy chủ.';
                    messageEl.className = 'error';
                });
            });
        });
    </script>
</body>
</html>