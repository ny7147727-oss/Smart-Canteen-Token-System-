package com.smartcanteen;

import com.smartcanteen.database.dao.IUserDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.common.exception.ValidationException;
import com.smartcanteen.model.Role;
import com.smartcanteen.model.Student;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.AuthService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationFlowTest {

    // In-memory Mock DAO for isolated testing without live DB dependency
    static class MockUserDAO implements IUserDAO {
        private final Map<String, User> userDb = new HashMap<>();
        private boolean shouldThrowDbException = false;

        public void setShouldThrowDbException(boolean throwDb) {
            this.shouldThrowDbException = throwDb;
        }

        @Override
        public User authenticate(String username, String hashedPassword) {
            if (shouldThrowDbException) throw new DatabaseException("Connection timeout", new SQLException("Communications link failure"));
            User user = userDb.get(username.toLowerCase());
            if (user != null && user.getPassword().equals(hashedPassword)) {
                return user;
            }
            return null;
        }

        @Override
        public User authenticate(User user) {
            if (user == null) return null;
            return authenticate(user.getUsername(), user.getPassword());
        }

        @Override
        public boolean registerUser(User user) {
            if (shouldThrowDbException) throw new DatabaseException("Failed to register user", new SQLException("Communications link failure"));
            user.setUserId(userDb.size() + 1);
            userDb.put(user.getUsername().toLowerCase(), user);
            return true;
        }

        @Override
        public boolean usernameExists(String username) {
            if (shouldThrowDbException) throw new DatabaseException("Failed to check username existence: " + username, new SQLException("Communications link failure: Unable to reach localhost:3306"));
            return userDb.containsKey(username.toLowerCase());
        }

        @Override
        public User getUserById(int id) {
            return userDb.values().stream().filter(u -> u.getUserId() == id).findFirst().orElse(null);
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("RUNNING REGISTRATION FLOW VERIFICATION TESTS");
        System.out.println("==========================================================");

        MockUserDAO mockDAO = new MockUserDAO();
        // Seed existing user "rahul"
        Student existingStudent = new Student();
        existingStudent.setUsername("rahul");
        existingStudent.setPassword("hashedpassword");
        existingStudent.setFullName("Rahul Kumar");
        existingStudent.setRole(Role.STUDENT);
        mockDAO.registerUser(existingStudent);

        AuthService authService = new AuthService(mockDAO);

        // TEST 1: New unique username -> registration succeeds
        try {
            Student newStudent = new Student();
            newStudent.setFullName("Priya Sharma");
            newStudent.setRollNumber("CS2023002");
            newStudent.setUsername("priya_sharma");
            newStudent.setPassword("securePass123");
            newStudent.setEmail("priya@student.edu");
            newStudent.setPhone("9876543212");

            boolean success = authService.register(newStudent);
            if (success && mockDAO.usernameExists("priya_sharma")) {
                System.out.println("✔ TEST 1 PASSED: New unique username registered successfully.");
            } else {
                System.err.println("✖ TEST 1 FAILED: Expected registration success.");
            }
        } catch (Exception e) {
            System.err.println("✖ TEST 1 FAILED with exception: " + e.getMessage());
        }

        // TEST 2: Existing username -> ValidationException with clear message
        try {
            Student duplicateStudent = new Student();
            duplicateStudent.setFullName("Another Rahul");
            duplicateStudent.setRollNumber("CS2023099");
            duplicateStudent.setUsername("rahul"); // already exists
            duplicateStudent.setPassword("somePassword123");

            authService.register(duplicateStudent);
            System.err.println("✖ TEST 2 FAILED: Duplicate registration did not throw ValidationException.");
        } catch (ValidationException e) {
            if ("Username already exists. Please choose another username.".equals(e.getMessage())) {
                System.out.println("✔ TEST 2 PASSED: Correct validation message on duplicate username: \"" + e.getMessage() + "\"");
            } else {
                System.err.println("✖ TEST 2 FAILED: Unexpected message: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("✖ TEST 2 FAILED with unexpected exception type: " + e.getClass().getName());
        }

        // TEST 3: Database failure -> Throws DatabaseException with root cause
        try {
            mockDAO.setShouldThrowDbException(true);

            Student testStudent = new Student();
            testStudent.setFullName("Amit Kumar");
            testStudent.setRollNumber("ME2023010");
            testStudent.setUsername("amit_k");
            testStudent.setPassword("pass1234");

            authService.register(testStudent);
            System.err.println("✖ TEST 3 FAILED: Database exception was not thrown.");
        } catch (DatabaseException e) {
            System.out.println("✔ TEST 3 PASSED: Meaningful DatabaseException thrown with root cause: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Underlying Cause: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("✖ TEST 3 FAILED with unexpected exception type: " + e.getClass().getName());
        }

        System.out.println("==========================================================");
        System.out.println("ALL REGISTRATION FLOW TESTS COMPLETED!");
        System.out.println("==========================================================");
    }
}
