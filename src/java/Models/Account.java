package Models;

public class Account {
    private String username;
    private String password;
    private String role;
    private String fullname;
    private String phone;
    private String address;
    private String email;

    public Account(String username, String password, String role, String fullname, String phone, String address, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String newPass) {
        this.password = newPass;
        //throw new UnsupportedOperationException("Cant set password"); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
        //throw new UnsupportedOperationException("Cant set fullname"); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPhone(String phone) {
        this.phone = phone;
        //throw new UnsupportedOperationException("Cant set phone"); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setAddress(String address) {
        this.address = address;
        //throw new UnsupportedOperationException("Cant set address"); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}