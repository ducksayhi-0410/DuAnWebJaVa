<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%> <%-- === THÊM DÒNG IMPORT NÀY === --%>
<%@page import="Models.Voucher"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Mã giảm giá</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/admin-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        List<Voucher> voucherList = (List<Voucher>) request.getAttribute("voucherList");
        String error = (String) request.getAttribute("error");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        
        // === THÊM DÒNG ĐỊNH NGHĨA BIẾN NÀY ===
        DecimalFormat formatter = new DecimalFormat("###,###,###");
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Quản lý Mã giảm giá</h1>
        
        <% if (error != null) { %>
            <div class="cart-notification error"><%= error %></div>
        <% } %>

        <div class="admin-container">
            
            <section class="admin-form-section">
                <h2>Thêm Mã giảm giá mới</h2>
                <form action="admin-vouchers" method="POST">
                    <input type="hidden" name="action" value="add">
                    
                    <div class="form-group">
                        <label for="code">Mã (VD: SALE20, VIP50K)</label>
                        <input type="text" id="code" name="code" required>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="discount_type">Loại giảm</label>
                            <select id="discount_type" name="discount_type">
                                <option value="percentage">Phần trăm (%)</option>
                                <option value="fixed_amount">Tiền cố định (₫)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="discount_value">Giá trị (VD: 20 hoặc 50000)</label>
                            <input type="number" id="discount_value" name="discount_value" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="min_order_value">Đơn hàng tối thiểu (₫)</label>
                        <input type="number" id="min_order_value" name="min_order_value" value="0" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="max_usage">Tổng lượt sử dụng</label>
                        <input type="number" id="max_usage" name="max_usage" value="100" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="expiry_date">Ngày hết hạn (YYYY-MM-DDTHH:MM)</label>
                        <input type="datetime-local" id="expiry_date" name="expiry_date" required>
                    </div>

                    <button type="submit" class="btn-submit">Thêm Mã</button>
                </form>
            </section>
            
            <section class="admin-list-section">
                <h2>Danh sách Mã giảm giá (<%= voucherList != null ? voucherList.size() : 0 %>)</h2>
                
                <div style="max-height: 500px; overflow-y: auto;">
                    <table class="admin-list-table">
                        <thead>
                            <tr>
                                <th>Mã (Code)</th>
                                <th>Giảm giá</th>
                                <th>Đơn tối thiểu</th>
                                <th>Đã dùng / Tổng</th>
                                <th>Hết hạn</th>
                                <th>Người tạo</th>
                                <th>Xóa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (voucherList != null && !voucherList.isEmpty()) {
                                for (Voucher v : voucherList) {
                                    // Dòng này giờ sẽ hết lỗi
                                    String discountStr = "percentage".equals(v.getDiscountType()) 
                                            ? (long)v.getDiscountValue() + "%"
                                            : formatter.format(v.getDiscountValue()) + " ₫";
                            %>
                            <tr>
                                <td><strong><%= v.getCode() %></strong></td>
                                <td style="color: #a40000; font-weight: bold;"><%= discountStr %></td>
                                
                                <%-- Dòng này giờ sẽ hết lỗi --%>
                                <td><%= formatter.format(v.getMinOrderValue()) %> ₫</td>
                                
                                <td><%= v.getCurrentUsage() %> / <%= v.getMaxUsage() %></td>
                                <td><%= dateFormat.format(v.getExpiryDate()) %></td>
                                <td><%= v.getCreatedBy() %></td>
                                <td class="action-links">
                                    <a href="admin-vouchers?action=delete&id=<%= v.getId() %>" 
                                       class="link-delete" title="Xóa"
                                       onclick="return confirm('Bạn có chắc muốn xóa mã: <%= v.getCode() %>?');">
                                        <i class="fas fa-trash-alt"></i>
                                    </a>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="7">Chưa có mã giảm giá nào.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>