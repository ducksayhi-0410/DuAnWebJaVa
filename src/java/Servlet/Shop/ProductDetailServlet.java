package Servlet.Shop;

import Db.ProductDb;
import Models.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product-detail"})
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy ID sản phẩm từ URL
        String id = request.getParameter("productId");
        
        // 2. Khởi tạo ProductDb
        ProductDb db = new ProductDb();
        
        // 3. Gọi phương thức mới để lấy 1 sản phẩm
        Product product = db.getProductById(id);
        
        // 4. Gửi đối tượng sản phẩm sang file JSP
        request.setAttribute("product", product);
        
        // 5. Chuyển tiếp đến trang ProductDetail.jsp
        request.getRequestDispatcher("ProductDetail.jsp").forward(request, response);
    }
}