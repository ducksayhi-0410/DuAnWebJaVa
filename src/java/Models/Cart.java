package Models;


import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    // Lấy item theo ID sản phẩm
    private Item getItemById(int productId) {
        for (Item item : items) {
            if (item.getProduct().getId() == productId) {
                return item;
            }
        }
        return null;
    }

    // Thêm một item vào giỏ
    public void addItem(Item newItem) {
        Item existingItem = getItemById(newItem.getProduct().getId());
        
        if (existingItem != null) {
            // Nếu sản phẩm đã có, chỉ cộng thêm số lượng
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
        } else {
            // Nếu sản phẩm chưa có, thêm mới
            items.add(newItem);
        }
    }

    // Xóa một item khỏi giỏ
    public void removeItem(int productId) {
        Item item = getItemById(productId);
        if (item != null) {
            items.remove(item);
        }
    }
    
    // Cập nhật số lượng
    public void updateItemQuantity(int productId, int quantity) {
        Item item = getItemById(productId);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    // Lấy tổng tiền
    public double getTotalMoney() {
        double total = 0;
        for (Item item : items) {
            total += item.getQuantity() * item.getProduct().getPrice();
        }
        return total;
    }

    // Lấy toàn bộ danh sách
    public List<Item> getItems() {
        return items;
    }
    
    // Đếm số lượng loại sản phẩm trong giỏ
    public int getTotalItems() {
        return items.size();
    }
}