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

    // (Giữ nguyên hàm checkAdmin)
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
        
        // === THÊM DÒNG NÀY ĐỂ SỬA LỖI FONT KHI NHẬN ===
        request.setCharacterEncoding("UTF-8");
        // ===========================================
        
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
                    Account accToEdit = accountDb.getAccountByUsername(username);
                    request.setAttribute("accountToEdit", accToEdit);
                    break;
                case "delete":
                    handleDelete(request, response, accountDb, username);
                    return; 
                case "viewHistory":
                    loadHistory(request, username);
                    break;
            }
        }
        
        List<Account> allAccounts = accountDb.getAllAccounts();
        request.setAttribute("allAccounts", allAccounts);
        request.getRequestDispatcher("admin-customers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // === THÊM DÒNG NÀY ĐỂ SỬA LỖI FONT KHI GỬI ===
        request.setCharacterEncoding("UTF-8");
        // =========================================
        
        if (!checkAdmin(request)) {
            response.sendRedirect("products");
            return;
        }
        
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
    
    // (Giữ nguyên các hàm loadHistory, handleDelete, handleAdd, handleUpdate)
    // ...
    private void loadHistory(HttpServletRequest request, String username) {
        if (username != null) {
            OrderDb orderDb = new OrderDb();
            List<Order> orderList = orderDb.getOrdersByUsername(username);
            request.setAttribute("orderList", orderList);
            request.setAttribute("viewingUser", username); 
        }
    }
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, AccountDb db, String username) 
            throws IOException {
        HttpSession session = request.getSession();
        Account adminAcc = (Account) session.getAttribute("acc");
        if (adminAcc.getUsername().equals(username)) {
            session.setAttribute("adminError", "Bạn không thể tự xóa tài khoản của mình!");
        } else {
            boolean success = db.deleteAccount(username);
            if (!success) {
                session.setAttribute("adminError", "Xóa thất bại! Đã xảy ra lỗi CSDL.");
            }
        }
        response.sendRedirect("admin-customers");
    }
    private void handleAdd(HttpServletRequest request, HttpServletResponse response, AccountDb db) {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        if (db.checkUsernameExists(username)) {
            session.setAttribute("adminError", "Thêm thất bại! Tên đăng nhập '" + username + "' đã tồn tại.");
            return;
        }
        if (db.checkEmailExists(email)) {
            session.setAttribute("adminError", "Thêm thất bại! Email '" + email + "' đã tồn Tạ.");
            return;
        }
        try {
            String spendParam = request.getParameter("lifetime_spend");
            double lifetimeSpend = (spendParam == null || spendParam.isEmpty()) ? 0 : Double.parseDouble(spendParam);
            Account acc = new Account(
                username, request.getParameter("password"), request.getParameter("role"), 
                request.getParameter("fullname"), request.getParameter("phone"), 
                request.getParameter("address"), email,
                request.getParameter("customer_tier"), lifetimeSpend
            );
            db.addAccount(acc);
        } catch (NumberFormatException e) {
            session.setAttribute("adminError", "Thêm thất bại! Tổng chi tiêu phải là một con số.");
        }
    }
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, AccountDb db) {
        HttpSession session = request.getSession();
        String username = request.getParameter("username"); 
        Account adminAcc = (Account) session.getAttribute("acc");
        if (adminAcc.getUsername().equals(username) && !adminAcc.getRole().equals(request.getParameter("role"))) {
            session.setAttribute("adminError", "Bạn không thể tự thay đổi vai trò của mình!");
            return;
        }
        String password = request.getParameter("password");
        boolean updatePassword = (password != null && !password.isEmpty());
        try {
            String spendParam = request.getParameter("lifetime_spend");
            double lifetimeSpend = (spendParam == null || spendParam.isEmpty()) ? 0 : Double.parseDouble(spendParam);
            Account acc = new Account(
                username, password, request.getParameter("role"), 
                request.getParameter("fullname"), request.getParameter("phone"),
                request.getParameter("address"), request.getParameter("email"),
                request.getParameter("customer_tier"), lifetimeSpend
            );
            db.updateAccount(acc, updatePassword);
        } catch (NumberFormatException e) {
            session.setAttribute("adminError", "Cập nhật thất bại! Tổng chi tiêu phải là một con số.");
        }
    }
}