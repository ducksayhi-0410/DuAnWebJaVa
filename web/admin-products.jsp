<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.Category"%>
<%@page import="java.util.List"%>
<%@page import="Models.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Kho hàng & Danh mục</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* (Giữ nguyên CSS cũ) */
        .admin-container { display: flex; gap: 30px; }
        .admin-form-section { width: 35%; border: 1px solid #eee; padding: 20px; border-radius: 5px; height: fit-content; }
        .admin-list-section { width: 65%; }
        .admin-form-section h2 { margin-top: 0; padding-bottom: 10px; border-bottom: 1px solid #eee; }
        .admin-form-section .form-group { margin-bottom: 15px; }
        .admin-form-section .form-group label { margin-bottom: 5px; font-size: 13px; }
        .admin-form-section .form-group input,
        .admin-form-section .form-group select,
        .admin-form-section .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; }
        .admin-form-section .form-group textarea { min-height: 80px; }
        .admin-form-section .btn-submit { padding: 10px 20px; font-size: 14px; }
        .admin-list-table { width: 100%; border-collapse: collapse; font-size: 13px; }
        .admin-list-table th, .admin-list-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        .admin-list-table th { background-color: #f5f5f5; }
        .admin-list-table img { width: 50px; height: 50px; object-fit: cover; }
        .admin-list-table .action-links a { margin-right: 10px; font-weight: bold; }
        .action-links .link-delete { color: #d9534f; }
        .action-links .link-edit { color: #0275d8; }
        .btn-clear-form { text-decoration: none; display: inline-block; margin-left: 10px; font-size: 13px; }
        .image-or-divider { text-align: center; font-weight: bold; color: #777; margin: 10px 0; font-size: 12px; }
    </style>
</head>
<body>
    
    <%@ include file="WEB-INF/main-header.jspf" %>
    
    <%
        Product productToEdit = (Product) request.getAttribute("productToEdit");
        List<Product> productList = (List<Product>) request.getAttribute("productList");
        List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        boolean isEditMode = (productToEdit != null);
        String formAction = isEditMode ? "admin-products?action=update" : "admin-products?action=add";
    %>
    
    <main class="container">
        <h1 style="margin-top: 20px;">Quản lý Kho hàng</h1>
        
        <%
            String adminError = (String) session.getAttribute("adminError");
            if (adminError != null) {
                session.removeAttribute("adminError");
        %>
            <div class="cart-notification error"><%= adminError %></div>
        <% } %>

        <div class="admin-container">
            
            <section class="admin-form-section">
                
                <h2><%= isEditMode ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới" %></h2>
                <form action="<%= formAction %>" method="POST" enctype="multipart/form-data">
                    <% if (isEditMode) { %>
                        <input type="hidden" name="id" value="<%= productToEdit.getId() %>">
                        <input type="hidden" name="existingImageUrl" value="<%= (productToEdit.getImageUrl() != null) ? productToEdit.getImageUrl() : "" %>">
                    <% } %>
                    <div class="form-group">
                        <label for="name">Tên sản phẩm</label>
                        <input type="text" id="name" name="name" value="<%= isEditMode ? productToEdit.getName() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label for="manufacturer">Hãng sản xuất</label>
                        <input type="text" id="manufacturer" name="manufacturer" value="<%= isEditMode ? productToEdit.getManufacturer() : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="price">Giá bán (VND)</label>
                        <input type="number" id="price" name="price" value="<%= isEditMode ? (long)productToEdit.getPrice() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label for="quantity">Số lượng tồn kho</label>
                        <input type="number" id="quantity" name="quantity" value="<%= isEditMode ? productToEdit.getQuantity() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label for="categoryId">Danh mục</label>
                        <select id="categoryId" name="categoryId" required>
                            <option value="">-- Chọn danh mục --</option>
                            <% if (categoryList != null) {
                                for (Category c : categoryList) {
                                    boolean isSelected = isEditMode && c.getId() == productToEdit.getCategoryId();
                            %>
                                <option value="<%= c.getId() %>" <%= isSelected ? "selected" : "" %>>
                                    <%= c.getName() %>
                                </option>
                            <% }} %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="imageFile">1. Upload Tệp Ảnh (Ưu tiên cao nhất)</label>
                        <input type="file" id="imageFile" name="imageFile" accept="image/*">
                    </div>
                    <div class="image-or-divider">--- HOẶC ---</div>
                    <div class="form-group">
                        <label for="imageUrl">2. Dán Link hình ảnh (URL)</label>
                        <input type="text" id="imageUrl" name="imageUrl" 
                               value="<%= (isEditMode && productToEdit.getImageUrl() != null && !productToEdit.getImageUrl().startsWith("uploads/")) ? productToEdit.getImageUrl() : "" %>"
                               placeholder="http://...">
                        <% if(isEditMode && productToEdit.getImageUrl() != null) { %>
                            <small>Ảnh hiện tại: <%= productToEdit.getImageUrl() %></small>
                        <% } %>
                    </div>
                    <div class="form-group">
                        <label for="description">Mô tả sản phẩm</label>
                        <textarea id="description" name="description"><%= isEditMode ? productToEdit.getDescription() : "" %></textarea>
                    </div>
                    <button type="submit" class="btn-submit"><%= isEditMode ? "Cập nhật" : "Thêm mới" %></button>
                    <% if (isEditMode) { %>
                        <a href="admin-products" class="btn-clear-form">Hủy (Thêm mới)</a>
                    <% } %>
                </form>
                
                <hr style="margin: 30px 0;">
                
                <h2 style="margin-top: 0;">Quản lý Danh mục</h2>
                
                <form action="admin-products" method="POST">
                    <input type="hidden" name="action" value="addCategory">
                    <div class="form-group">
                        <label for="categoryName">Tên danh mục mới</label>
                        <input type="text" id="categoryName" name="name" required 
                               placeholder="VD: Rượu Vang Chile">
                    </div>
                    <button type="submit" class="btn-submit" style="width:100%;">
                        <i class="fas fa-plus"></i> Thêm Danh mục
                    </button>
                </form>

                <h3 style="margin-top: 25px; font-size: 16px;">Danh sách hiện có</h3>
                <div style="max-height: 250px; overflow-y: auto; border: 1px solid #eee;">
                    <table class="admin-list-table" style="width:100%; font-size: 14px;">
                        <thead>
                            <tr>
                                <th>Tên</th>
                                <th>Xóa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (categoryList != null && !categoryList.isEmpty()) {
                                for (Category c : categoryList) {
                            %>
                            <tr>
                                <td><%= c.getName() %></td>
                                <td class="action-links" style="width: 50px; text-align: center;">
                                    <a href="admin-products?action=deleteCategory&id=<%= c.getId() %>"
                                       class="link-delete"
                                       title="Xóa danh mục"
                                       onclick="return confirm('Cảnh báo: Xóa danh mục sẽ thất bại nếu có sản phẩm đang thuộc về nó. \nBạn có chắc chắn muốn xóa <%= c.getName() %>?');">
                                        <i class="fas fa-trash-alt"></i>
                                    </a>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="2">Chưa có danh mục.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                </section>
            
            <section class="admin-list-section">
                <h2>Danh sách sản phẩm (<%= productList != null ? productList.size() : 0 %>)</h2>
                
                <table class="admin-list-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Ảnh</th>
                            <th>Tên sản phẩm</th>
                            <th>Giá</th>
                            <th>SL</th>
                            <th>Hãng</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (productList != null && !productList.isEmpty()) {
                            for (Product p : productList) {
                        %>
                        <tr>
                            <td><%= p.getId() %></td>
                            <td><img src="<%= p.getImageUrl() %>" alt="<%= p.getName() %>"></td>
                            <td><%= p.getName() %></td>
                            <td><%= formatter.format(p.getPrice()) %> d</td>
                            <td><%= p.getQuantity() %></td>
                            <td><%= p.getManufacturer() %></td>
                            <td class="action-links">
                                <a href="admin-products?action=edit&id=<%= p.getId() %>" class="link-edit">Sửa</a>
                                
                                <a href="admin-products?action=delete&id=<%= p.getId() %>" 
                                   class="link-delete" 
                                   onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');">Xóa</a>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr>
                            <td colspan="7">Không có sản phẩm nào.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </section>
        </div>
    </main>

    <%@ include file="WEB-INF/main-footer.jspf" %>
</body>
</html>