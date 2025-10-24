package Servlet; // Hoặc package filter của bạn

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

// Áp dụng filter này cho TẤT CẢ các URL bắt đầu bằng /profile, /edit-profile, /change-password, /my-orders
@WebFilter(urlPatterns = {"/profile", "/edit-profile", "/change-password", "/my-orders"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // Lấy session (không tạo mới nếu chưa có)

        // Kiểm tra xem session có tồn tại và có chứa 'acc' (tài khoản) không
        if (session != null && session.getAttribute("acc") != null) {
            // Người dùng đã đăng nhập, cho phép họ đi tiếp
            chain.doFilter(request, response);
        } else {
            // Người dùng chưa đăng nhập, chuyển hướng họ về trang đăng nhập
            res.sendRedirect(req.getContextPath() + "/DangNhap.jsp");
        }
    }

    // Các phương thức init và destroy có thể để trống
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}