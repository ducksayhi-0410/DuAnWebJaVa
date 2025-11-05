package Servlet.Admin;

import Db.VoucherDb;
import Models.Account;
import Models.Voucher;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminVouchersServlet", urlPatterns = {"/admin-vouchers"})
public class AdminVouchersServlet extends HttpServlet {

    private boolean checkPermission(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Account acc = (Account) session.getAttribute("acc");
        if (acc == null) return false;
        return "admin".equals(acc.getRole()) || "nhanvien".equals(acc.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkPermission(request)) {
            response.sendRedirect("products");
            return;
        }
        
        VoucherDb db = new VoucherDb();
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                db.deleteVoucher(id);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse ID voucher khi xóa: " + e.getMessage());
            }
            response.sendRedirect("admin-vouchers");
            return;
        }

        // Mặc định là tải danh sách
        List<Voucher> voucherList = db.getAllVouchers();
        request.setAttribute("voucherList", voucherList);
        request.getRequestDispatcher("admin-vouchers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkPermission(request)) {
            response.sendRedirect("products");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            VoucherDb db = new VoucherDb();
            try {
                String code = request.getParameter("code");
                String type = request.getParameter("discount_type");
                double value = Double.parseDouble(request.getParameter("discount_value"));
                double minOrder = Double.parseDouble(request.getParameter("min_order_value"));
                int maxUsage = Integer.parseInt(request.getParameter("max_usage"));
                String expiryDateStr = request.getParameter("expiry_date"); // Format: yyyy-MM-ddTHH:mm
                
                // Chuyển đổi String datetime-local sang Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date expiryDate = sdf.parse(expiryDateStr);
                
                // Lấy người tạo từ session
                HttpSession session = request.getSession();
                Account acc = (Account) session.getAttribute("acc");
                String createdBy = acc.getUsername();
                
                boolean success = db.addVoucher(code, type, value, minOrder, maxUsage, expiryDate, createdBy);
                if (!success) {
                    request.setAttribute("error", "Thêm thất bại! Mã (code) có thể đã bị trùng.");
                }
                
            } catch (NumberFormatException | ParseException e) {
                request.setAttribute("error", "Lỗi định dạng số hoặc ngày tháng. Vui lòng kiểm tra lại.");
                System.err.println("Lỗi parse form voucher: " + e.getMessage());
            }
        }
        
        // Tải lại trang (nếu có lỗi, request.setAttribute sẽ hiển thị)
        doGet(request, response);
    }
}