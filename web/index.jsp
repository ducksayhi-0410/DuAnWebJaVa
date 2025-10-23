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
    </head>
    <body>

        <%-- === SỬA LỖI: Dùng static include (dấu <%@) để chia sẻ biến Java === --%>
        <%@ include file="WEB-INF/main-header.jspf" %>
        
        <%
            List<Product> productList = (List<Product>) request.getAttribute("productList");
            if (productList == null) {
                productList = new ArrayList<>();
            }

            List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
            if (categoryList == null) {
                categoryList = new ArrayList<>();
            }

            Integer totalProductsObj = (Integer) request.getAttribute("totalProducts");
            if (totalProductsObj == null) {
                totalProductsObj = 0;
            }
            int totalProducts = totalProductsObj;

            DecimalFormat formatter = new DecimalFormat("###,###,###");
        %>

        <main class="container">
            <div class="breadcrumbs">
                <a href="products">Trang chủ</a> / <span>Rượu Vang Sủi</span>
            </div>
            <div class="page-title">
                 <h1>Rượu Vang Sủi</h1>
                <p>Rượu vang sủi bọt đã trở thành thức uống không thể thiếu để khuấy động không khí tiệc tùng.
                    Hãy cùng khám phá danh mục những dòng vang sủi được ưa chuộng nhất hiện nay với hương vị thơm ngon, sống động, say đắm lòng người.</p>
            </div>

            <div class="content-wrapper">
                <aside class="sidebar">
                    <div class="filter-group">
                        <h3>DANH MỤC SẢHẨM</h3>
                        <ul id="category-filter-list">
                            <%-- Các biến p_searchQuery, p_sort... bây giờ đã tồn tại --%>
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
                         <p>Hiển thị <%= productList.size()%> trong số <%= totalProducts%> kết quả</p>
                         <div class="view-options">
                            <button class="view-btn active"><i class="fas fa-th"></i></button>
                            <button class="view-btn"><i class="fas fa-list"></i></button>
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
                            if (!productList.isEmpty()) {
                                 for (Product p : productList) {
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
    
    <form action="add-to-cart" method="POST" style="display:inline-block;" class="ajax-cart-form">
        <input type="hidden" name="productId" value="<%= p.getId() %>">
        <input type="hidden" name="quantity" value="1">
        
        <input type="hidden" name="isAjax" value="true">
        
        <button type="button" class="btn-add-cart">Thêm vào giỏ</button>
    </form>
</div>
                            </div>
                        </div>
                        <%
                                 }
                            } else {
                        %>
                        <p>Không tìm thấy sản phẩm nào phù hợp với tiêu chí của bạn.</p>
                        <%
                            }
                        %>
                    </div>
                </section>
            </div>
        </main>
        
        <%-- === SỬA LỖI: Dùng static include (dấu <%@) === --%>
        <%@ include file="WEB-INF/main-footer.jspf" %>

    </body>
</html>