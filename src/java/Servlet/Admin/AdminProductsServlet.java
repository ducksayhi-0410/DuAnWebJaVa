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

@WebServlet(name = "AdminProductsServlet", urlPatterns = {"/admin-products"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class AdminProductsServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "uploads";

    private boolean checkAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Account acc = (Account) session.getAttribute("acc");
        if (acc == null) return false;
        return "admin".equals(acc.getRole());
    }

    // ========================================================
    // ===          CHỈNH SỬA LẠI doGet                    ===
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
                    handleDelete(request, response, productDb);
                    return; 
                
                case "edit": // Sửa sản phẩm
                    handleEdit(request, response, productDb);
                    break; // break để chạy tiếp xuống loadProductList
                
                // --- THÊM LOGIC MỚI ---
                case "deleteCategory":
                    handleDeleteCategory(request, response, new CategoryDb());
                    return; // Chuyển hướng sau khi xóa
                // --- KẾT THÚC ---
            }
        }
        
        // Mặc định: Hiển thị danh sách sản phẩm VÀ danh mục
        loadProductList(request, response);
    }

    // ========================================================
    // ===          CHỈNH SỬA LẠI doPost                   ===
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
                    
                // --- THÊM LOGIC MỚI ---
                case "addCategory":
                    handleAddCategory(request, response, new CategoryDb());
                    break;
                // --- KẾT THÚC ---
            }
        }
        
        // Tải lại trang sau khi POST
        response.sendRedirect("admin-products");
    }

    /**
     * Tải trang quản lý (danh sách sản phẩm, danh mục)
     */
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

    // [ ... Giữ nguyên các hàm: handleEdit, handleDelete, handleAdd, handleUpdate, getImageUrl ... ]
    
    // ========================================================
    // ===    CÁC HÀM XỬ LÝ SẢN PHẨM (GIỮ NGUYÊN)         ===
    // ========================================================
    private void handleEdit(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) {
        String id = request.getParameter("id");
        Product productToEdit = productDb.getProductById(id);
        request.setAttribute("productToEdit", productToEdit);
    }
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) throws IOException {
        String id = request.getParameter("id");
        boolean success = productDb.deleteProduct(id);
        if (!success) {
            request.getSession().setAttribute("adminError", "Không thể xóa sản phẩm này (có thể đã tồn tại trong đơn hàng).");
        }
        response.sendRedirect("admin-products");
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
            productDb.addProduct(p);
        } catch (NumberFormatException e) { System.err.println("Lỗi parse số khi thêm sản phẩm: " + e.getMessage()); }
    }
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, ProductDb productDb) throws IOException, ServletException {
        try {
            Product p = new Product();
            String id = request.getParameter("id");
            String existingImageUrl = request.getParameter("existingImageUrl");
            String imageUrl = getImageUrl(request, existingImageUrl);
            p.setId(Integer.parseInt(id)); 
            p.setName(request.getParameter("name"));
            p.setDescription(request.getParameter("description"));
            p.setPrice(Double.parseDouble(request.getParameter("price")));
            p.setImageUrl(imageUrl);
            p.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            p.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            p.setManufacturer(request.getParameter("manufacturer"));
            productDb.updateProduct(p);
        } catch (NumberFormatException e) { System.err.println("Lỗi parse số khi cập nhật sản phẩm: " + e.getMessage()); }
    }
    private String getImageUrl(HttpServletRequest request, String existingImageUrl) throws IOException, ServletException {
        String imageUrlToSave = null;
        Part filePart = request.getPart("imageFile");
        String fileName = filePart.getSubmittedFileName();
        if (fileName != null && !fileName.isEmpty()) {
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
            File fileUploadDir = new File(uploadFilePath);
            if (!fileUploadDir.exists()) {
                fileUploadDir.mkdirs();
            }
            filePart.write(uploadFilePath + File.separator + fileName);
            imageUrlToSave = UPLOAD_DIR + "/" + fileName;
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
    
    // ========================================================
    // ===    CÁC HÀM XỬ LÝ DANH MỤC (MỚI)               ===
    // ========================================================
    
    
     
    private void handleDeleteCategory(HttpServletRequest request, HttpServletResponse response, CategoryDb db) 
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = db.deleteCategory(id);
            if (!success) {
                // Đặt thông báo lỗi (thường là do vi phạm khóa ngoại)
                request.getSession().setAttribute("adminError", "Không thể xóa danh mục. (Có thể đang có sản phẩm thuộc danh mục này).");
            }
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse ID khi xóa danh mục: " + e.getMessage());
        }
        response.sendRedirect("admin-products"); // Tải lại trang
    }
    
    /**
   
     */
    private void handleAddCategory(HttpServletRequest request, HttpServletResponse response, CategoryDb db) 
            throws IOException {
        String name = request.getParameter("name");
        if (name != null && !name.trim().isEmpty()) {
            db.addCategory(name);
        }
       
    }
}