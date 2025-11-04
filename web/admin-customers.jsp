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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* (Sử dụng CSS từ admin-products.jsp) */
        .admin-container { display: flex; gap: 30px; }
        .admin-form-section { width: 35%; border: 1px solid #eee; padding: 20px; border-radius: 5px; height: fit-content; }
        .admin-list-section { width: 65%; }
        .admin-form-section h2 { margin-top: 0; padding-bottom: 10px; border-bottom: 1px solid #eee; }
        .admin-form-section .form-group { margin-bottom: 15px; }
        .admin-form-section .form-group label { margin-bottom: 5px; font-size: 13px; }
        .admin-form-section .form-group input,
        .admin-form-section .form-group select { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; }
        .admin-form-section .btn-submit { padding: 10px 20px; font-size: 14px; }
        .admin-list-table { width: 100%; border-collapse: collapse; font-size: 13px; }
        .admin-list-table th, .admin-list-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        .admin-list-table th { background-color: #f5f5f5; }
        .admin-list-table .action-links a { margin-right: 10px; font-weight: bold; }
        .action-links .link-delete { color: #d9534f; }
        .action-links .link-edit { color: #0275d8; }
        .action-links .link-view { color: #5cb85c; }
        .btn-clear-form { text-decoration: none; display: inline-block; margin-left: 10px; font-size: 13px; }
        
        /* CSS cho phần lịch sử (lấy từ my-orders.jsp) */
        .history-section { margin-top: 30px; padding-top: 20px; border-top: 2px solid #a40000; }
        .order-list { display: flex; flex-direction: column; gap: 20px; }
        .order-card { border: 1px solid #eee; border-radius: 8px; background-color: #fff; overflow: hidden; }
        .order-card-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: #f9f9f9; border-bottom: 1px solid #eee; }
        .order-card-header h3 { margin: 0; font-size: 16px; color: #a40000; }
        .order-card-header span { font-size: 14px; color: #555; font-weight: 400; }
        .order-status { font-weight: bold; font-size: 14px; padding: 5px 10px; border-radius: 5px; background-color: #e0e0e0; color: #333; }
        .order-card-body { padding: 20px; }
        .order-item-list { width: 100%; border-collapse: collapse; }
        .order-item-list th, .order-item-list td { padding: 10px 0; border-bottom: 1px solid #f0f0f0; text-align: left; font-size: 14px; }
        .order-item-list th { font-weight: 500; color: #666; }
        .order-item-list tr:last-child td { border-bottom: none; }
        .order-item-list .item-total { font-weight: bold; color: #a40000; text-align: right; }
        .order-card-footer { display: flex; justify-content: flex-end; align-items: center; padding: 15px 20px; background-color: #f9f9f9; border-top: 1px solid #eee; }
        .order-card-footer span { font-size: 18px; font-weight: bold; color: #a40000; }
    </style>
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
        
        <%-- Hiển thị lỗi (nếu có) --%>
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
                            <%-- Chỉ cho phép set 2 vai trò này --%>
                            <option value="customer" <%= (isEditMode && "customer".equals(accountToEdit.getRole())) ? "selected" : "" %>>
                                Khách hàng (customer)
                            </option>
                            <option value="nhanvien" <%= (isEditMode && "nhanvien".equals(accountToEdit.getRole())) ? "selected" : "" %>>
                                Nhân viên (nhanvien)
                            </option>
                            <%-- An toàn: Không cho phép set 'admin' từ giao diện --%>
                            <% if (isEditMode && "admin".equals(accountToEdit.getRole())) { %>
                                <option value="admin" selected>Quản trị viên (admin)</option>
                            <% } %>
                        </select>
                    </div>

                    <button type="submit" class="btn-submit"><%= isEditMode ? "Cập nhật" : "Thêm mới" %></button>
                    <% if (isEditMode) { %>
                        <a href="admin-customers" class="btn-clear-form">Hủy (Thêm mới)</a>
                    <% } %>
                </form>
            </section>
            
            <section class="admin-list-section">
                <h2>Danh sách tài khoản (<%= allAccounts != null ? allAccounts.size() : 0 %>)</h2>
                
                <div style="max-height: 500px; overflow-y: auto;">
                    <table class="admin-list-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Password</th>
                                <th>Role</th>
                                <th>Full Name</th>
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
                                
                                <td class="action-links">
                                    <a href="admin-customers?action=edit&username=<%= a.getUsername() %>" 
                                       class="link-edit" title="Sửa"><i class="fas fa-edit"></i></a>
                                    
                                    <a href="admin-customers?action=delete&username=<%= a.getUsername() %>" 
                                       class="link-delete" title="Xóa"
                                       onclick="return confirm('Bạn có chắc chắn muốn xóa tài khoản <%= a.getUsername() %>?');">
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
                                <td colspan="5">Không có tài khoản nào.</td>
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
                    } // Kết thúc lặp đơn hàng
                } // Kết thúc else
            %>
            </div>
        </section>
        <% } %>
        
    </main>

    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>