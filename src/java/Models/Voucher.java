package Models;

import java.util.Date;

public class Voucher {
    private int id;
    private String code;
    private String discountType; // 'percentage' hoặc 'fixed_amount'
    private double discountValue;
    private double minOrderValue;
    private int maxUsage;
    private int currentUsage;
    private Date expiryDate;
    private String createdBy;

    // Constructor đầy đủ
    public Voucher(int id, String code, String discountType, double discountValue, 
                   double minOrderValue, int maxUsage, int currentUsage, 
                   Date expiryDate, String createdBy) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.maxUsage = maxUsage;
        this.currentUsage = currentUsage;
        this.expiryDate = expiryDate;
        this.createdBy = createdBy;
    }

    // Getters
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
    public double getMinOrderValue() { return minOrderValue; }
    public int getMaxUsage() { return maxUsage; }
    public int getCurrentUsage() { return currentUsage; }
    public Date getExpiryDate() { return expiryDate; }
    public String getCreatedBy() { return createdBy; }

    // (Bạn có thể thêm Setters nếu cần)
}