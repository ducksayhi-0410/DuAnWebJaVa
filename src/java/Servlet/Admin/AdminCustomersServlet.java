package Servlet.Admin;

import Db.AccountDb;
import Db.OrderDb;
import Models.Account;
import Models.Order;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminCustomersServlet", urlPatterns = {"/admin-customers"})
public class AdminCustomersServlet extends HttpServlet {

    /**
     * Kiểm tra quyền Admin
     */
    private boolean checkAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Account acc = (Account) session.getAttribute("acc");
        if (acc == null) return false;
        return "admin".equals(acc.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAdmin(request)) {
            response.sendRedirect("products");
            return;
        }
        
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        AccountDb accountDb = new AccountDb();
        
        if (action != null) {
            switch (action) {
                case "edit":
                    // Tải thông tin tài khoản lên form Sửa
                    Account accToEdit = accountDb.getAccountByUsername(username);
                    request.setAttribute("accountToEdit", accToEdit);
                    break;
                    
                case "delete":
                    // Xử lý xóa tài khoản
                    handleDelete(request, response, accountDb, username);
                    return; // Chuyển hướng trong hàm con

                case "viewHistory":
                    // Xem lịch sử (giữ nguyên)
                    loadHistory(request, username);
                    break;
            }
        }
        
        // Luôn luôn tải danh sách tài khoản
        List<Account> allAccounts = accountDb.getAllAccounts();
        request.setAttribute("allAccounts", allAccounts);
        
        request.getRequestDispatcher("admin-customers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAdmin(request)) {
            response.sendRedirect("products");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        AccountDb accountDb = new AccountDb();
        
        if (action != null) {
            switch (action) {
                case "add":
                    handleAdd(request, response, accountDb);
                    break;
                case "update":
                    handleUpdate(request, response, accountDb);
                    break;
            }
        }
        
        response.sendRedirect("admin-customers");
    }
    
    /**
     * Tải lịch sử đơn hàng (cho action=viewHistory)
     */
    private void loadHistory(HttpServletRequest request, String username) {
        if (username != null) {
            OrderDb orderDb = new OrderDb();
            List<Order> orderList = orderDb.getOrdersByUsername(username);
            request.setAttribute("orderList", orderList);
            request.setAttribute("viewingUser", username); 
        }
    }
    
    /**
     * Xử lý xóa
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, AccountDb db, String username) 
            throws IOException {
        
        HttpSession session = request.getSession();
        Account adminAcc = (Account) session.getAttribute("acc");

        // Bảo vệ: Không cho admin tự xóa mình
        if (adminAcc.getUsername().equals(username)) {
            session.setAttribute("adminError", "Bạn không thể tự xóa tài khoản của mình!");
        } else {
            boolean success = db.deleteAccount(username);
            if (!success) {
                session.setAttribute("adminError", "Xóa thất bại! (Có thể tài khoản này đã có đơn hàng).");
            }
        }
        response.sendRedirect("admin-customers");
    }
    
    /**
     * Xử lý thêm
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response, AccountDb db) {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        
        // 1. Kiểm tra tồn tại
        if (db.checkUsernameExists(username)) {
            session.setAttribute("adminError", "Thêm thất bại! Tên đăng nhập '" + username + "' đã tồn tại.");
            return;
        }
        if (db.checkEmailExists(email)) {
            session.setAttribute("adminError", "Thêm thất bại! Email '" + email + "' đã tồn tại.");
            return;
        }
        
        // 2. Tạo tài khoản
        Account acc = new Account(
            username, 
            request.getParameter("password"), 
            request.getParameter("role"), 
            request.getParameter("fullname"), 
            request.getParameter("phone"), 
            null, // Address
            email
        );
        
        db.addAccount(acc);
    }
    
    /**
     * Xử lý cập nhật
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, AccountDb db) {
        HttpSession session = request.getSession();
        String username = request.getParameter("username"); // Username không đổi
        Account adminAcc = (Account) session.getAttribute("acc");
        
        // Bảo vệ: Không cho admin tự sửa vai trò của mình
        if (adminAcc.getUsername().equals(username) && !adminAcc.getRole().equals(request.getParameter("role"))) {
            session.setAttribute("adminError", "Bạn không thể tự thay đổi vai trò của mình!");
            return;
        }

        String password = request.getParameter("password");
        boolean updatePassword = (password != null && !password.isEmpty());

        Account acc = new Account(
            username, 
            password, // Sẽ được bỏ qua nếu trống
            request.getParameter("role"), 
            request.getParameter("fullname"), 
            request.getParameter("phone"),
            request.getParameter("address"), // Thêm address
            request.getParameter("email")
        );
        
        db.updateAccount(acc, updatePassword);
    }
}