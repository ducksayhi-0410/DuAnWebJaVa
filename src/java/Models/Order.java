package Models;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private String username;
    private Date orderDate; // Dùng java.util.Date
    private double totalMoney;
    private String status;
    
    // Thêm các trường thông tin giao hàng
    private String shippingAddress;
    private String shippingPhone;

    // Một đối tượng Order sẽ chứa danh sách các chi tiết của nó
    private List<OrderDetail> details;

    // Constructors, Getters, Setters
    
    public Order() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public double getTotalMoney() { return totalMoney; }
    public void setTotalMoney(double totalMoney) { this.totalMoney = totalMoney; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> details) { this.details = details; }

    
}