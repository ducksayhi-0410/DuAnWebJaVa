<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.OrderDetail"%>
<%@page import="Models.Order"%>
<%@page import="Models.Account"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Tài khoản</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/admin-style.css">
    <%-- Đảm bảo bạn đã link Font Awesome --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
   
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        List<Account> allAccounts = (List<Account>) request.getAttribute("allAccounts");
        List<Order> orderList = (List<Order>) request.getAttribute("orderList");
        Account accountToEdit = (Account) request.getAttribute("accountToEdit");
        String viewingUser = (String) request.getAttribute("viewingUser");
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm 'ngày' dd/MM/yyyy");
        
        boolean isEditMode = (accountToEdit != null);
        String formAction = isEditMode ? "admin-customers?action=update" : "admin-customers?action=add";
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Quản lý Tài khoản</h1>
        
        <%
            String adminError = (String) session.getAttribute("adminError");
            if (adminError != null) {
                session.removeAttribute("adminError");
        %>
            <div class="cart-notification error"><%= adminError %></div>
        <% } %>

        <div class="admin-container">
            
            <section class="admin-form-section">
                <h2><%= isEditMode ? "Chỉnh sửa tài khoản" : "Thêm tài khoản mới" %></h2>
                
                <form action="<%= formAction %>" method="POST">
                    
                    <div class="form-group">
                        <label for="username">Tên đăng nhập (Không thể đổi)</label>
                        <input type="text" id="username" name="username" 
                               value="<%= isEditMode ? accountToEdit.getUsername() : "" %>" 
                               <%= isEditMode ? "readonly" : "required" %>
                               style="<%= isEditMode ? "background-color: #f0f0f0;" : "" %>">
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Mật khẩu</label>
                        <input type="text" id="password" name="password" 
                               placeholder="<%= isEditMode ? "(Để trống nếu không muốn đổi)" : "" %>"
                               <%= isEditMode ? "" : "required" %>>
                    </div>
                    
                    <div class="form-group">
                        <label for="fullname">Họ và Tên</label>
                        <input type="text" id="fullname" name="fullname" 
                               value="<%= (isEditMode && accountToEdit.getFullname() != null) ? accountToEdit.getFullname() : "" %>">
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" 
                               value="<%= (isEditMode && accountToEdit.getEmail() != null) ? accountToEdit.getEmail() : "" %>" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="phone">Số điện thoại</label>
                        <input type="tel" id="phone" name="phone" 
                               value="<%= (isEditMode && accountToEdit.getPhone() != null) ? accountToEdit.getPhone() : "" %>">
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Địa chỉ</label>
                        <input type="text" id="address" name="address" 
                               value="<%= (isEditMode && accountToEdit.getAddress() != null) ? accountToEdit.getAddress() : "" %>">
                    </div>
                    
                    <div class="form-group">
                        <label for="role">Vai trò</label>
                        <select id="role" name="role" required>
                            <option value="customer" <%= (isEditMode && "customer".equals(accountToEdit.getRole())) ? "selected" : "" %>>
                                Khách hàng (customer)
                            </option>
                            <option value="nhanvien" <%= (isEditMode && "nhanvien".equals(accountToEdit.getRole())) ? "selected" : "" %>>
                                Nhân viên (nhanvien)
                            </option>
                            <% if (isEditMode && "admin".equals(accountToEdit.getRole())) { %>
                                <option value="admin" selected>Quản trị viên (admin)</option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="customer_tier">Hạng Thành viên</label>
                            <select id="customer_tier" name="customer_tier" required>
                                <option value="dong" <%= (isEditMode && "dong".equals(accountToEdit.getCustomerTier())) ? "selected" : "" %>>
                                    Đồng
                                </option>
                                <option value="bac" <%= (isEditMode && "bac".equals(accountToEdit.getCustomerTier())) ? "selected" : "" %>>
                                    Bạc
                                </option>
                                <option value="vang" <%= (isEditMode && "vang".equals(accountToEdit.getCustomerTier())) ? "selected" : "" %>>
                                    Vàng
                                </option>
                                <option value="kimcuong" <%= (isEditMode && "kimcuong".equals(accountToEdit.getCustomerTier())) ? "selected" : "" %>>
                                    Kim Cương
                                </option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="lifetime_spend">Tổng chi tiêu (VND)</label>
                            <input type="number" id="lifetime_spend" name="lifetime_spend" 
                                   value="<%= isEditMode ? (long)accountToEdit.getLifetimeSpend() : "0" %>" required>
                        </div>
                    </div>

                    <button type="submit" class="btn-submit"><%= isEditMode ? "Cập nhật" : "Thêm mới" %></button>
                    <% if (isEditMode) { %>
                        <a href="admin-customers" class="btn-clear-form">Hủy (Thêm mới)</a>
                    <% } %>
                </form>
            </section>
            
            <section class="admin-list-section">
                <h2>Danh sách tài khoản (<%= allAccounts != null ? allAccounts.size() : 0 %>)</h2>
                
                <div style="max-height: 500px; overflow-y: auto; overflow-x: auto;">
                    <table class="admin-list-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Password</th>
                                <th>Role</th>
                                <th>Full Name</th>
                                <th>Hạng</th>
                                <th>Tổng chi tiêu</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (allAccounts != null && !allAccounts.isEmpty()) {
                                for (Account a : allAccounts) {
                                    boolean isViewingThis = a.getUsername().equals(viewingUser);
                                    boolean isEditingThis = isEditMode && a.getUsername().equals(accountToEdit.getUsername());
                            %>
                            <tr style="<%= (isViewingThis || isEditingThis) ? "background-color: #fdf0f0;" : "" %>">
                                <td><strong><%= a.getUsername() %></strong></td>
                                <td><%= a.getPassword() %></td>
                                <td style="text-transform: capitalize;"><%= a.getRole() %></td>
                                <td><%= (a.getFullname() != null) ? a.getFullname() : "<i>(Chưa có)</i>" %></td>
                                <td style="text-transform: capitalize;"><%= a.getCustomerTier() %></td>
                                <td><%= formatter.format(a.getLifetimeSpend()) %> ₫</td>
                                
                                <td class="action-links">
                                    <%-- === SỬA LỖI ICON TẠI ĐÂY === --%>
                                    <a href="admin-customers?action=edit&username=<%= a.getUsername() %>" 
                                       class="link-edit" title="Sửa"><i class="fas fa-edit"></i></a>
                                    
                                    <a href="admin-customers?action=delete&username=<%= a.getUsername() %>" 
                                       class="link-delete" title="Xóa"
                                       onclick="return confirm('CẢNH BÁO: Thao tác này sẽ xóa vĩnh viễn tài khoản <%= a.getUsername() %> và TOÀN BỘ lịch sử đơn hàng/giỏ hàng của họ. \nBạn có chắc chắn muốn xóa?');">
                                        <i class="fas fa-trash-alt"></i>
                                    </a>
                                    
                                    <% if ("customer".equals(a.getRole())) { %>
                                        <a href="admin-customers?action=viewHistory&username=<%= a.getUsername() %>" 
                                           class="link-view" title="Xem lịch sử mua hàng">
                                            <i class="fas fa-history"></i>
                                        </a>
                                    <% } %>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="7">Không có tài khoản nào.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
        
        <% if (orderList != null && viewingUser != null) { %>
        <section class="history-section">
            <h2>Lịch sử mua hàng của: <%= viewingUser %> (<%= orderList.size() %> đơn)</h2>
            <div class="order-list" style="max-height: 600px; overflow-y: auto;">
            <%
                if (orderList.isEmpty()) {
            %>
                <p>Khách hàng này chưa có đơn hàng nào.</p>
            <%
                } else {
                    for (Order order : orderList) {
            %>
                <div class="order-card">
                    <div class="order-card-header">
                        <h3>Mã đơn: #<%= order.getId() %> <span>(<%= dateFormat.format(order.getOrderDate()) %>)</span></h3>
                        <span class="order-status"><%= order.getStatus() %></span>
                    </div>
                    <div class="order-card-body">
                        <table class="order-item-list">
                            <thead>
                                <tr>
                                    <th>Sản phẩm</th> <th>Đơn giá</th> <th>SL</th> <th style="text-align: right;">Tạm tính</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (OrderDetail detail : order.getDetails()) { %>
                                <tr>
                                    <td><%= detail.getProductName() %></td>
                                    <td><%= formatter.format(detail.getPrice()) %> ₫</td>
                                    <td>x <%= detail.getQuantity() %></td>
                                    <td class="item-total"><%= formatter.format(detail.getPrice() * detail.getQuantity()) %> ₫</td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <div class="order-card-footer">
                        <span><strong>Tổng cộng:</strong> <%= formatter.format(order.getTotalMoney()) %> ₫</span>
                    </div>
                </div>
            <%
                    } 
                } 
            %>
            </div>
        </section>
        <% } %>
        
    </main>

    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>