package Servlet.Admin;

import Db.CategoryDb; 
import Db.ProductDb;
import Models.Account;
import Models.Category;
import Models.Product;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;

// === THÊM CÁC IMPORT MỚI ĐỂ SỬA LỖI UPLOAD & XÓA FILE ===
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;
// === KẾT THÚC THÊM IMPORT ===

@WebServlet(name = "AdminProductsServlet", urlPatterns = {"/admin-products"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class AdminProductsServlet extends HttpServlet {
    
    // Đảm bảo UPLOAD_DIR khớp với lựa chọn của bạn ("uploads" hoặc "img")
    private static final String UPLOAD_DIR = "img"; // <-- KIỂM TRA LẠI ĐÂY

    private boolean checkAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Account acc = (Account) session.getAttribute("acc");
        if (acc == null) return false;
        return "admin".equals(acc.getRole());
    }

    // ========================================================
    // ===          doGet (Giữ nguyên)                      ===
    // ========================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAdmin(request)) {
            response.sendRedirect("products");
            return;
        }
        
        String action = request.getParameter("action");

        if (action != null) {
            ProductDb productDb = new ProductDb();
            
            switch (action) {
                case "delete": // Xóa sản phẩm
                    // === SỬA HÀM NÀY ĐỂ TRUYỀN REQUEST VÀO ===
                    handleDelete(request, response, productDb);
                    return; 
                
                case "edit": // Sửa sản phẩm
                    handleEdit(request, response, productDb);
                    break;
                
                case "deleteCategory":
                    handleDeleteCategory(request, response, new CategoryDb());
                    return;
            }
        }
        
        loadProductList(request, response);
    }

    // =DànG (Giữ nguyên)                     ===
    // ========================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAdmin(request)) {
            response.sendRedirect("products");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if (action != null) {
            ProductDb productDb = new ProductDb();
            
            switch (action) {
                case "add": // Thêm sản phẩm
                    handleAdd(request, response, productDb);
                    break;
                case "update": // Cập nhật sản phẩm
                    handleUpdate(request, response, productDb);
                    break;
                    
                case "addCategory":
                    handleAddCategory(request, response, new CategoryDb());
                    break;
            }
        }
        
        response.sendRedirect("admin-products");
    }

    // ========================================================
    // ===    HÀM `handleDelete` (ĐÃ NÂNG CẤP)             ===
    // ========================================================
    
    /**
     * Xử lý xóa sản phẩm (bao gồm cả xóa tệp ảnh)
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) 
            throws IOException {
        
        String id = request.getParameter("id");
        
        // --- BƯỚC 1: LẤY THÔNG TIN SẢN PHẨM TRƯỚC KHI XÓA ---
        Product productToDelete = productDb.getProductById(id); //
        
        if (productToDelete != null) {
            String imageUrl = productToDelete.getImageUrl(); //
            
            // --- BƯỚC 2: KIỂM TRA VÀ XÓA TỆP ẢNH ---
            // Chỉ xóa nếu ảnh là tệp tải lên (nằm trong UPLOAD_DIR), không xóa link http
            if (imageUrl != null && imageUrl.startsWith(UPLOAD_DIR)) {
                try {
                    // Lấy đường dẫn thư mục gốc của webapp (ví dụ: C:\...(build\web))
                    String applicationPath = request.getServletContext().getRealPath("");
                    // Tạo đường dẫn đầy đủ đến tệp ảnh
                    Path imagePath = Paths.get(applicationPath, imageUrl);
                    
                    // Xóa tệp
                    Files.deleteIfExists(imagePath);
                    System.out.println("Đã xóa tệp ảnh: " + imagePath.toString());
                    
                } catch (Exception e) {
                    System.err.println("Lỗi khi xóa tệp ảnh: " + e.getMessage());
                }
            }
        }

        // --- BƯỚC 3: XÓA SẢN PHẨM KHỎI CSDL ---
        boolean success = productDb.deleteProduct(id); //
        
        if (!success) {
            request.getSession().setAttribute("adminError", "Không thể xóa sản phẩm này (có thể đã tồn tại trong đơn hàng).");
        }
        
        response.sendRedirect("admin-products");
    }

    // ========================================================
    // ===    CÁC HÀM KHÁC (GIỮ NGUYÊN)                    ===
    // ========================================================

    private void loadProductList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ProductDb productDb = new ProductDb();
        CategoryDb categoryDb = new CategoryDb();
        
        List<Product> productList = productDb.getProductsFiltered("", "", "", "", "");
        List<Category> categoryList = categoryDb.getAllCategories();
        
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        
        request.getRequestDispatcher("admin-products.jsp").forward(request, response);
    }
    
    private void handleEdit(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) {
        String id = request.getParameter("id");
        Product productToEdit = productDb.getProductById(id); //
        request.setAttribute("productToEdit", productToEdit);
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) throws IOException, ServletException {
        try {
            Product p = new Product();
            String imageUrl = getImageUrl(request, null); 
            p.setName(request.getParameter("name"));
            p.setDescription(request.getParameter("description"));
            p.setPrice(Double.parseDouble(request.getParameter("price")));
            p.setImageUrl(imageUrl);
            p.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            p.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            p.setManufacturer(request.getParameter("manufacturer"));
            productDb.addProduct(p); //
        } catch (NumberFormatException e) { System.err.println("Lỗi parse số khi thêm sản phẩm: " + e.getMessage()); }
    }
    
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) throws IOException, ServletException {
        try {
            Product p = new Product();
            String id = request.getParameter("id");
            String existingImageUrl = request.getParameter("existingImageUrl");
            
            // --- XỬ LÝ XÓA ẢNH CŨ NẾU TẢI ẢNH MỚI ---
            // (Phần này code của bạn chưa có, tôi thêm vào)
            Product oldProduct = productDb.getProductById(id); //
            String oldImageUrl = (oldProduct != null) ? oldProduct.getImageUrl() : existingImageUrl; //
            
            String newImageUrl = getImageUrl(request, oldImageUrl); // Gọi hàm đã sửa lỗi
            
            // Nếu ảnh mới khác ảnh cũ VÀ ảnh cũ là tệp tải lên
            if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl) && oldImageUrl.startsWith(UPLOAD_DIR)) {
                 try {
                    String applicationPath = request.getServletContext().getRealPath("");
                    Path imagePath = Paths.get(applicationPath, oldImageUrl);
                    Files.deleteIfExists(imagePath);
                    System.out.println("Đã xóa tệp ảnh cũ khi cập nhật: " + imagePath.toString());
                 } catch (Exception e) {
                     System.err.println("Lỗi khi xóa tệp ảnh cũ: " + e.getMessage());
                 }
            }
            // --- KẾT THÚC XỬ LÝ XÓA ẢNH CŨ ---
            
            p.setId(Integer.parseInt(id)); 
            p.setName(request.getParameter("name"));
            p.setDescription(request.getParameter("description"));
            p.setPrice(Double.parseDouble(request.getParameter("price")));
            p.setImageUrl(newImageUrl); // Dùng ảnh mới
            p.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            p.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            p.setManufacturer(request.getParameter("manufacturer"));
            productDb.updateProduct(p); //
        } catch (NumberFormatException e) { System.err.println("Lỗi parse số khi cập nhật sản phẩm: " + e.getMessage()); }
    }
    
    private String getImageUrl(HttpServletRequest request, String existingImageUrl) 
            throws IOException, ServletException {
        
        String imageUrlToSave = null;
        Part filePart = request.getPart("imageFile");
        String fileName = filePart.getSubmittedFileName();
        
        if (fileName != null && !fileName.isEmpty()) {
            
            String applicationPath = request.getServletContext().getRealPath("");
            Path uploadPath = Paths.get(applicationPath, UPLOAD_DIR);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String safeFileName = Paths.get(fileName).getFileName().toString();
            Path destinationFile = uploadPath.resolve(safeFileName);
            
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            imageUrlToSave = UPLOAD_DIR + "/" + safeFileName;
            
        } else {
            String imageUrlFromText = request.getParameter("imageUrl");
            if (imageUrlFromText != null && !imageUrlFromText.isEmpty()) {
                imageUrlToSave = imageUrlFromText;
            } else {
                imageUrlToSave = existingImageUrl;
            }
        }
        
        return imageUrlToSave;
    }
    
    private void handleDeleteCategory(HttpServletRequest request, HttpServletResponse response, CategoryDb db) 
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = db.deleteCategory(id); //
            if (!success) {
                request.getSession().setAttribute("adminError", "Không thể xóa danh mục. (Có thể đang có sản phẩm thuộc danh mục này).");
            }
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse ID khi xóa danh mục: " + e.getMessage());
        }
        response.sendRedirect("admin-products");
    }
    
    private void handleAddCategory(HttpServletRequest request, HttpServletResponse response, CategoryDb db) 
            throws IOException {
        String name = request.getParameter("name");
        if (name != null && !name.trim().isEmpty()) {
            db.addCategory(name); //
        }
       
    }
}