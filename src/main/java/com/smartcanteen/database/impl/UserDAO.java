package com.smartcanteen.database.impl;

import com.smartcanteen.database.dao.IUserDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.model.Admin;
import com.smartcanteen.model.Role;
import com.smartcanteen.model.Student;
import com.smartcanteen.model.User;
import com.smartcanteen.database.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserDAO implements IUserDAO {

    @Override
    public User authenticate(String username, String hashedPassword) {
        String sql = "SELECT user_id, username, password, role, full_name, email, phone, roll_number, created_at " +
                     "FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] SQL Exception while authenticating user '" + username + "': " + e.getMessage());
            throw new DatabaseException("Failed to authenticate user: " + username + " (" + e.getMessage() + ")", e);
        }
        return null;
    }

    @Override
    public User authenticate(User user) {
        if (user == null) {
            return null;
        }
        return authenticate(user.getUsername(), user.getPassword());
    }

    @Override
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, role, full_name, email, phone, roll_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole() != null ? user.getRole().name() : Role.STUDENT.name());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPhone());
            stmt.setString(7, user.getRollNumber());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("[UserDAO] SQL Exception while registering user '" + (user != null ? user.getUsername() : "null") + "': " + e.getMessage());
            throw new DatabaseException("Failed to register user: " + (user != null ? user.getUsername() : "null") + " (" + e.getMessage() + ")", e);
        }
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] SQL Exception while checking username existence for '" + username + "': " + e.getMessage());
            throw new DatabaseException("Failed to check username existence: " + username + " (" + e.getMessage() + ")", e);
        }
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT user_id, username, password, role, full_name, email, phone, roll_number, created_at " +
                     "FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] SQL Exception while retrieving user with ID '" + id + "': " + e.getMessage());
            throw new DatabaseException("Failed to retrieve user with ID: " + id + " (" + e.getMessage() + ")", e);
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        Role role = (roleStr != null) ? Role.valueOf(roleStr) : Role.STUDENT;
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String rollNumber = rs.getString("roll_number");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        if (role == Role.STUDENT) {
            return new Student(userId, username, password, role, fullName, email, phone, rollNumber, createdAt);
        } else if (role == Role.ADMIN) {
            return new Admin(userId, username, password, role, fullName, email, phone, rollNumber, createdAt);
        } else {
            return new User(userId, username, password, role, fullName, email, phone, rollNumber, createdAt);
        }
    }
}
