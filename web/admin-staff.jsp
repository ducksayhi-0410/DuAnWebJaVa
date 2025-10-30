<%@page import="Models.Account"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Nhân viên</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <style>
            .admin-container {
                padding: 20px;
                max-width: 1200px;
                margin: 20px auto;
            }
            .admin-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }
            .admin-table th, .admin-table td {
                border: 1px solid #ddd;
                padding: 10px;
                text-align: left;
            }
            .admin-table th {
                background-color: #f5f5f5;
            }
            .admin-table .actions a {
                margin-right: 10px;
            }
            .btn-add-new {
                display: inline-block;
                padding: 10px 15px;
                background: #a40000;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="admin-container">
            <h1>Quản lý Nhân viên</h1>
            <a href="employees?action=add" class="btn-add-new">Thêm nhân viên mới</a>

            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Họ và Tên</th>
                        <th>Email</th>
                        <th>Số điện thoại</th>
                        <th>Địa chỉ</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <% List<Account> list = (List<Account>) request.getAttribute("employeeList");
                    if (list != null && !list.isEmpty()) {
                        for (Account acc : list) {%>
                    <tr>
                        <td><%= acc.getUsername()%></td>
                        <td><%= acc.getFullname()%></td>
                        <td><%= acc.getEmail()%></td>
                        <td><%= acc.getPhone()%></td>
                        <td><%= acc.getAddress()%></td>
                        <td class="actions">
                            <a href="employees?action=edit&username=<%= acc.getUsername()%>" class="btn-detail">Sửa</a>
                            <a href="employees?action=delete&username=<%= acc.getUsername()%>" 
                               onclick="return confirm('Bạn có chắc chắn muốn xóa nhân viên <%= acc.getUsername()%> không?');"
                               style="color: red;">Xóa</a>
                        </td>
                    </tr>
                    <%     }
                } else { %>
                    <tr>
                        <td colspan="6">Không có nhân viên nào.</td>
                    </tr>
                    <% }%>
                </tbody>
            </table>

            <a href="${pageContext.request.contextPath}/products" style="display:inline-block; margin-top: 20px;">&larr; Quay lại trang chủ</a>
        </div>
    </body>
</html>