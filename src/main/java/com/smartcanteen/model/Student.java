package com.smartcanteen.model;

import java.time.LocalDateTime;

public class Student extends User {

    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    public Student(int userId, String username, String password, String fullName, String email, String phone, String rollNumber, LocalDateTime createdAt) {
        super(userId, username, password, Role.STUDENT, fullName, email, phone, rollNumber, createdAt);
    }

    public Student(int userId, String username, String password, Role role, String fullName, String email, String phone, String rollNumber, LocalDateTime createdAt) {
        super(userId, username, password, role, fullName, email, phone, rollNumber, createdAt);
    }

    @Override
    public String toString() {
        return "Student{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", rollNumber='" + getRollNumber() + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
