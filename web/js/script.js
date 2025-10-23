// Chờ cho toàn bộ nội dung trang web được tải xong
document.addEventListener('DOMContentLoaded', function() {

    // ==============================================
    // Chức năng cho Cửa sổ Đăng nhập (Modal)
    // ==============================================
    // (Giữ nguyên)
    const loginLink = document.getElementById('login-link');
    const loginModal = document.getElementById('login-modal');
    
    // Kiểm tra xem modal có tồn tại không
    if (loginModal) {
        const closeModalBtn = loginModal.querySelector('.close-btn');

        // Hàm để ẩn modal
        const hideModal = function() {
            loginModal.classList.add('hidden');
        }

        // Hiện modal khi click vào link "Đăng nhập"
        // Phải kiểm tra loginLink có tồn tại không (vì khi đăng nhập, nó sẽ biến mất)
        if (loginLink) {
            loginLink.addEventListener('click', function(event) {
                event.preventDefault(); // Ngăn link chuyển trang
                loginModal.classList.remove('hidden');
            });
        }

        // Ẩn modal khi click nút đóng (X)
        if (closeModalBtn) {
            closeModalBtn.addEventListener('click', hideModal);
        }

        // Ẩn modal khi click vào lớp phủ bên ngoài
        loginModal.addEventListener('click', function(event) {
            // Chỉ ẩn khi click vào chính lớp phủ, không phải nội dung bên trong
            if (event.target === loginModal) {
                hideModal();
            }
        });

        // Ẩn modal khi nhấn phím Escape
        document.addEventListener('keydown', function(event) {
            if (event.key === 'Escape' && !loginModal.classList.contains('hidden')) {
                hideModal();
            }
        });
    } // Kết thúc if (loginModal)


    // ==============================================
    // Chức năng cho các nút chuyển đổi chế độ xem
    // ==============================================
    // (Giữ nguyên - đây là code cosmetic)
    const viewButtons = document.querySelectorAll('.view-btn');
    
    viewButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Xóa class 'active' khỏi tất cả các nút
            viewButtons.forEach(btn => btn.classList.remove('active'));
            // Thêm class 'active' vào nút vừa được click
            this.classList.add('active');
            
            // Logic để chuyển đổi giao diện sẽ được thêm ở đây
        });
    });


    // ==============================================
    // PHẦN LỌC GIÁ VÀ TÌM KIẾM BẰNG JAVASCRIPT ĐÃ BỊ XÓA
    // ==============================================
    //
    // Lý do: Tất cả các chức năng lọc (tìm kiếm, lọc giá, lọc danh mục)
    // hiện đã được xử lý bởi SERVER (ProductServlet.java)
    // bằng cách tải lại trang với các tham số URL (?categoryId=... & searchQuery=...)
    //
    // Giữ lại code JS cũ ở đây sẽ gây xung đột với logic mới của server.
    //
    // === AJAX Add to Cart (cho trang index.jsp) ===
    const allCartForms = document.querySelectorAll('.ajax-cart-form');
    const notificationPopup = document.getElementById('cart-notification');

    allCartForms.forEach(form => {
        const button = form.querySelector('.btn-add-cart');
        
        button.addEventListener('click', function() {
            
            // 1. Lấy dữ liệu từ form
            const formData = new FormData(form);
            
            // 2. Gửi yêu cầu AJAX (Fetch)
            fetch('add-to-cart', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json()) // Yêu cầu Servlet trả về JSON
            .then(data => {
                // 3. Xử lý kết quả JSON
                if (data.success) {
                    // Cập nhật số lượng trên icon giỏ hàng
                    const cartCountElement = document.querySelector('.header-actions a[href="cart"]');
                    if (cartCountElement) {
                        cartCountElement.innerHTML = `<i class="fas fa-shopping-cart"></i> GIỎ HÀNG (${data.cartTotalItems})`;
                    }
                    
                    // Hiển thị thông báo
                    if (notificationPopup) {
                        notificationPopup.innerText = data.message; // Cập nhật text
                        notificationPopup.classList.add('show');
                        setTimeout(() => {
                            notificationPopup.classList.remove('show');
                        }, 2000); // Ẩn sau 2 giây
                    }
                } else {
                    // Xử lý lỗi (ví dụ: hết hàng)
                    alert(data.message || 'Có lỗi xảy ra, không thể thêm vào giỏ.');
                }
            })
            .catch(error => {
                console.error('Lỗi khi thêm vào giỏ:', error);
                alert('Lỗi kết nối, vui lòng thử lại.');
            });
        });
    });
    // (Dấu }); cuối file của bạn nằm ở đây)
});