<%@page import="Models.Category"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="Models.Product"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Rượu Vang Sủi - Trang Web Bán Rượu</title>
        <link rel="stylesheet" href="css/style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
 
        <style>
            .pagination-container {
                text-align: center;
                margin-top: 30px;
                padding-top: 20px;
                border-top: 1px solid #eee;
            }
            .pagination-container a {
                display: inline-block;
                padding: 8px 14px;
                margin: 0 4px;
                border: 1px solid #ddd;
                background-color: #fff;
                color: #333;
                text-decoration: none;
                border-radius: 4px;
                font-weight: 500;
            }
            .pagination-container a:hover {
                background-color: #f5f5f5;
                text-decoration: none;
            }
            .pagination-container a.active {
                background-color: #a40000;
                color: white;
                border-color: #a40000;
                cursor: default;
            }
        </style>
    </head>
    <body>

        <%@ include file="WEB-INF/main-header.jspf" %>
        
        <%
            // (Code lấy cartSuccess, cartError giữ nguyên)
            String cartSuccess = (String) session.getAttribute("cartSuccess");
            String cartError = (String) session.getAttribute("cartError");
            if (cartSuccess != null) session.removeAttribute("cartSuccess");
            if (cartError != null) session.removeAttribute("cartError");
            
            // (Code lấy categoryList giữ nguyên)
            List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
            if (categoryList == null) categoryList = new ArrayList<>();

            DecimalFormat formatter = new DecimalFormat("###,###,###");
            
            // === LOGIC PHÂN TRANG BẰNG JSP ===
            
            // 1. Lấy TẤT CẢ sản phẩm (đây là lý do chậm)
            List<Product> allProducts = (List<Product>) request.getAttribute("productList");
            if (allProducts == null) {
                allProducts = new ArrayList<>();
            }
            
            int totalProducts = allProducts.size(); // Dùng size() thay vì biến từ Servlet

            // 2. Định nghĩa số sản phẩm mỗi trang (Bạn có thể đổi số 9)
            final int PRODUCTS_PER_PAGE = 9;

            // 3. Lấy trang hiện tại từ URL (do người dùng bấm)
            int currentPage = 1;
            try {
                if (request.getParameter("page") != null) {
                    currentPage = Integer.parseInt(request.getParameter("page"));
                }
            } catch (NumberFormatException e) {
                currentPage = 1; // Nếu nhập bậy, về trang 1
            }

            // 4. Tính toán
            int totalPages = (int) Math.ceil((double) totalProducts / PRODUCTS_PER_PAGE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            if (currentPage < 1) currentPage = 1;

            // 5. Tính toán vị trí bắt đầu (offset)
            int startIndex = (currentPage - 1) * PRODUCTS_PER_PAGE;
            int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, totalProducts);
            
            // 6. Lấy danh sách sản phẩm cho trang NÀY
            // (Hàm subList() của Java sẽ làm việc này)
            List<Product> productListOnPage = (totalProducts > 0) 
                                                ? allProducts.subList(startIndex, endIndex) 
                                                : new ArrayList<>();
            
            // (p_categoryId, p_searchQuery... đã được lấy từ main-header.jspf)
        %>
        
        <main class="container">
        
            <% if (cartSuccess != null) { %>
                <div class="cart-notification success"><%= cartSuccess %></div>
            <% } %>
            <% if (cartError != null) { %>
                <div class="cart-notification error"><%= cartError %></div>
            <% } %>
  
           <div class="breadcrumbs">
                <a href="products">Trang chủ</a> / <span>Rượu Vang Sủi</span>
            </div>
            <div class="page-title">
                 <h1>Rượu Vang Sủi</h1>
                 <p>Rượu vang sủi bọt đã trở thành thức uống không thể thiếu... (Giữ nguyên mô tả)</p>
            </div>

            <div class="content-wrapper">
                <aside class="sidebar">
                    <div class="filter-group">
                         <h3>DANH MỤC SẢN PHẨM</h3>
                        <ul id="category-filter-list">
                             <li><a href="products?categoryId=&searchQuery=<%= p_searchQuery%>&minPrice=<%= p_minPrice%>&maxPrice=<%= p_maxPrice%>&sort=<%= p_sort%>">
                                   Tất cả sản phẩm
                                </a></li>
                             <%
                                 if (!categoryList.isEmpty()) {
                                        for (Category c : categoryList) {
                                 %>
                             <li><a href="products?categoryId=<%= c.getId()%>&searchQuery=<%= p_searchQuery%>&minPrice=<%= p_minPrice%>&maxPrice=<%= p_maxPrice%>&sort=<%= p_sort%>"
                                class="<%= String.valueOf(c.getId()).equals(p_categoryId) ? "active" : ""%>">
                                    <%= c.getName()%>
                                </a></li>
                           <%
                                        }
                                     }
                           %>
                        </ul>
                    </div>
                     <div class="filter-group">
                         <h3>TÌM KIẾM</h3>
                        <form class="search-in-category" action="products" method="GET">
                             <input type="text" name="searchQuery" placeholder="Nhập tên rượu..." value="<%= p_searchQuery%>">
                            <input type="hidden" name="categoryId" value="<%= p_categoryId%>">
                             <input type="hidden" name="minPrice" value="<%= p_minPrice%>">
                             <input type="hidden" name="maxPrice" value="<%= p_maxPrice%>">
                            <input type="hidden" name="sort" value="<%= p_sort%>">
                             <button type="submit"><i class="fas fa-search"></i></button>
                         </form>
                    </div>
                    <div class="filter-group">
                         <h3>LỌC THEO KHOẢNG GIÁ</h3>
                        <ul id="price-filter-list">
                            <li><a href="products?minPrice=0&maxPrice=499999&categoryId=<%= p_categoryId%>&searchQuery=<%= p_searchQuery%>&sort=<%= p_sort%>">Dưới 500K</a></li>
                            <li><a href="products?minPrice=500000&maxPrice=1000000&categoryId=<%= p_categoryId%>&searchQuery=<%= p_searchQuery%>&sort=<%= p_sort%>">Từ 500K - 1 triệu</a></li>
                            <li><a href="products?minPrice=1000000&maxPrice=2000000&categoryId=<%= p_categoryId%>&searchQuery=<%= p_searchQuery%>&sort=<%= p_sort%>">Từ 1 triệu - 2 triệu</a></li>
                            <li><a href="products?minPrice=2000000&maxPrice=3000000&categoryId=<%= p_categoryId%>&searchQuery=<%= p_searchQuery%>&sort=<%= p_sort%>">Từ 2 triệu - 3 triệu</a></li>
                            <li><a href="products?minPrice=20000000&maxPrice=&categoryId=<%= p_categoryId%>&searchQuery=<%= p_searchQuery%>&sort=<%= p_sort%>">Trên 20 triệu</a></li>
                            <li><a href="products?minPrice=&maxPrice=&categoryId=<%= p_categoryId%>&searchQuery=<%= p_searchQuery%>&sort=<%= p_sort%>" id="show-all-prices">Tất cả khoảng giá</a></li>
                        </ul>
                     </div>
                </aside>

                <section class="product-listing">
                    <div class="toolbar">
                         <%-- (Cập nhật text hiển thị) --%>
                         <p>Hiển thị <%= productListOnPage.size() %> trong số <%= totalProducts %> kết quả</p>
                         
                         <div class="view-options">
                            <button class="view-btn active"><i class="fas fa-th"></i></button>
                            <button class="view-btn"><i class="fas fa-list"></i></button>
                            
                         <%-- Form sắp xếp này cũng không cần 'page' --%>
                         <form action="products" method="GET" id="sortForm" style="display: inline-block;">
                                <input type="hidden" name="categoryId" value="<%= p_categoryId%>">
                                 <input type="hidden" name="searchQuery" value="<%= p_searchQuery%>">
                                 <input type="hidden" name="minPrice" value="<%= p_minPrice%>">
                                 <input type="hidden" name="maxPrice" value="<%= p_maxPrice%>">
                                <select name="sort" id="sorting" onchange="document.getElementById('sortForm').submit();">
                                    <option value="" <%= p_sort.isEmpty() ? "selected" : ""%>>Thứ tự mặc định</option>
                                    <option value="price-asc" <%= "price-asc".equals(p_sort) ? "selected" : ""%>>Giá: Thấp đến Cao</option>
                                    <option value="price-desc" <%= "price-desc".equals(p_sort) ? "selected" : ""%>>Giá: Cao đến Thấp</option>
                                </select>
                            </form>
                        </div>
                     </div>

                    <div class="product-grid">
                        <%
                            // === SỬA VÒNG LẶP: CHỈ LẶP QUA DANH SÁCH CỦA TRANG NÀY ===
                            if (!productListOnPage.isEmpty()) {
                                 for (Product p : productListOnPage) {
                        %>
                        <div class="product-card" data-price="<%= p.getPrice()%>">
                           <div class="product-image">
                                <img src="<%= p.getImageUrl()%>" alt="<%= p.getName()%>">
                             </div>
                            <div class="product-info">
                                 <div class="product-meta">
                                     <span>Mã SP: <%= p.getId()%></span>
                                </div>
                                <h3 class="product-name"><%= p.getName()%></h3>
                                <p class="product-description"><%= p.getDescription()%></p>
                                <p class="product-quantity">Số lượng còn lại: <%= p.getQuantity() %></p>
                                <p class="product-price"><%= formatter.format(p.getPrice())%> ₫</p>
                                <div class="product-actions">
                                    <a href="product-detail?productId=<%= p.getId() %>" class="btn-detail">Chi tiết</a>
                                    <form action="add-to-cart" method="POST" style="display:inline-block;">
                                        <input type="hidden" name="productId" value="<%= p.getId() %>">
                                        <input type="hidden" name="quantity" value="1">
                                        <input type="hidden" name="source" value="index">
                                        <button type="submit" class="btn-add-cart">Thêm vào giỏ</button>
                                    </form>
                                </div>
                            </div>
                         </div>
                        <%
                                 } // Đóng vòng lặp for
                             } else {
                        %>
                        <p>Không tìm thấy sản phẩm nào phù hợp với tiêu chí của bạn.</p>
                        <%
                             } // Đóng 'else'
                        %>
                    </div>
                    
                    <div class="pagination-container">
                        <%
                            // Chỉ hiển thị nếu có nhiều hơn 1 trang
                            if (totalPages > 1) {
                                // Vòng lặp để vẽ các nút 1, 2, 3...
                                for (int i = 1; i <= totalPages; i++) {
                                    String activeClass = (i == currentPage) ? "active" : "";
                                    
                                    // Tạo link, giữ nguyên tất cả bộ lọc
                                    String pageLink = String.format("products?page=%d&categoryId=%s&searchQuery=%s&minPrice=%s&maxPrice=%s&sort=%s",
                                        i, p_categoryId, p_searchQuery, p_minPrice, p_maxPrice, p_sort);
                        %>
                                    <a href="<%= pageLink %>" class="<%= activeClass %>">
                                        <%= i %>
                                    </a>
                        <%
                                }
                            }
                        %>
                    </div>
                    </section>
            </div>
        </main>
  
       
 
        <%@ include file="WEB-INF/main-footer.jspf" %>

    </body>
</html>