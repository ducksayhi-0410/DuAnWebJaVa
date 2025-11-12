package Servlet.Shop;

import Db.CategoryDb;
import Db.ProductDb;
import Models.Category;
import Models.Product;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductServlet", urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Hỗ trợ tìm kiếm tiếng Việt
        
        // 1. Lấy tất cả các tham số lọc từ URL
        String categoryId = request.getParameter("categoryId");
        String searchQuery = request.getParameter("searchQuery");
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("maxPrice");
        String sortOrder = request.getParameter("sort");

    
        // Chuyển đổi NULL thành chuỗi rỗng ("")
        // Việc này đảm bảo ProductDb nhận được giá trị nhất quán
        // giống như khi bấm link filter trên JSP
        categoryId = (categoryId == null) ? "" : categoryId;
        searchQuery = (searchQuery == null) ? "" : searchQuery;
        minPrice = (minPrice == null) ? "" : minPrice;
        maxPrice = (maxPrice == null) ? "" : maxPrice;
        sortOrder = (sortOrder == null) ? "" : sortOrder;
        // --- KẾT THÚC SỬA LỖI ---

        // 2. Khởi tạo các đối tượng Db
        ProductDb productDb = new ProductDb();
        CategoryDb categoryDb = new CategoryDb();

        // 3. Lấy dữ liệu từ CSDL
        // Các phương thức Db bây giờ sẽ nhận "" thay vì null
        List<Product> productList = productDb.getProductsFiltered(categoryId, searchQuery, minPrice, maxPrice, sortOrder);
        int totalProducts = productDb.getProductCount(categoryId, searchQuery, minPrice, maxPrice);
        List<Category> categoryList = categoryDb.getAllCategories();

        // 4. Gửi TẤT CẢ dữ liệu sang JSP
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("totalProducts", totalProducts);
        
        // 5. Gửi lại các bộ lọc đã chọn để JSP "nhớ"
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("currentSearchQuery", searchQuery);
        request.setAttribute("currentMinPrice", minPrice);
        request.setAttribute("currentMaxPrice", maxPrice);
        request.setAttribute("currentSort", sortOrder);

        // 6. Chuyển tiếp tới trang JSP
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}