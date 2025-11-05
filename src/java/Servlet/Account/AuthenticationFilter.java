package Servlet.Account;

import Db.AccountDb; // <-- Thêm import
import Models.Account;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// === CẬP NHẬT: Thêm 2 trang mới vào Filter ===
@WebFilter(urlPatterns = {
    "/profile", "/edit-profile", "/change-password", "/my-orders", 
    "/order-status", "/membership-perks" 
})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); 

        Account acc = null;
        if (session != null) {
            acc = (Account) session.getAttribute("acc");
        }

        if (acc != null) {
            // === LOGIC LÀM MỚI (REFRESH) SESSION ===
            // 1. Lấy username từ session CŨ
            String username = acc.getUsername();
            
            // 2. Tải lại dữ liệu MỚI NHẤT từ CSDL
            AccountDb db = new AccountDb();
            Account freshAcc = db.getAccountByUsername(username);
            
            // 3. Đặt lại (overwrite) acc trong session bằng dữ liệu mới
            if (freshAcc != null) {
                session.setAttribute("acc", freshAcc);
            }
            // === KẾT THÚC LOGIC LÀM MỚI ===
            
            // 4. Cho phép đi tiếp
            chain.doFilter(request, response);
            
        } else {
            // Người dùng chưa đăng nhập, chuyển hướng về trang đăng nhập
            res.sendRedirect(req.getContextPath() + "/DangNhap.jsp");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}