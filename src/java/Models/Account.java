package Models;

public class Account {
    private String username;
    private String password;
    private String role;
    private String fullname;
    private String phone;
    private String address;
    private String email;
    
    // === THÊM 2 TRƯỜNG MỚI ===
    private String customerTier; // Bạc, Vàng, Kim Cương
    private double lifetimeSpend; // Tổng chi tiêu

    // === CẬP NHẬT CONSTRUCTOR ===
    public Account(String username, String password, String role, String fullname, 
                   String phone, String address, String email, 
                   String customerTier, double lifetimeSpend) { // Thêm 2 tham số
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.customerTier = customerTier; // Thêm
        this.lifetimeSpend = lifetimeSpend; // Thêm
    }
    
    // Constructor cũ (nếu bạn cần ở đâu đó, ví dụ ReportDb)
    public Account(String username, String password, String role, String fullname, 
                   String phone, String address, String email) {
        this(username, password, role, fullname, phone, address, email, "dong", 0);
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getFullname() { return fullname; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }

    // === THÊM GETTERS MỚI ===
    public String getCustomerTier() { return customerTier; }
    public double getLifetimeSpend() { return lifetimeSpend; }
    
    // Setters (bạn có thể triển khai đầy đủ nếu muốn)
    public void setPassword(String newPass) { this.password = newPass; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    
    // (Thêm setters mới nếu cần)
    public void setCustomerTier(String tier) { this.customerTier = tier; }
    public void setLifetimeSpend(double spend) { this.lifetimeSpend = spend; }
}