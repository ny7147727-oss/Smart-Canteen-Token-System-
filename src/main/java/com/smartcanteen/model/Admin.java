package com.smartcanteen.model;

import java.time.LocalDateTime;

public class Admin extends User {

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(int userId, String username, String password, String fullName, String email, String phone, LocalDateTime createdAt) {
        super(userId, username, password, Role.ADMIN, fullName, email, phone, null, createdAt);
    }

    public Admin(int userId, String username, String password, Role role, String fullName, String email, String phone, String rollNumber, LocalDateTime createdAt) {
        super(userId, username, password, role, fullName, email, phone, rollNumber, createdAt);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
