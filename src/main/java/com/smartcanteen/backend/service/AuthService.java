package com.smartcanteen.backend.service;

import com.smartcanteen.database.dao.IUserDAO;
import com.smartcanteen.database.impl.UserDAO;
import com.smartcanteen.common.exception.AuthenticationException;
import com.smartcanteen.common.exception.ValidationException;
import com.smartcanteen.model.Role;
import com.smartcanteen.model.Student;
import com.smartcanteen.model.User;
import com.smartcanteen.common.util.PasswordHasher;
import com.smartcanteen.common.util.SessionManager;
import com.smartcanteen.common.util.Validator;

public class AuthService {

    private final IUserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(IUserDAO userDAO) {
        this.userDAO = (userDAO != null) ? userDAO : new UserDAO();
    }

    public User login(String username, String plainPassword) throws AuthenticationException {
        if (!Validator.isNotEmpty(username) || !Validator.isNotEmpty(plainPassword)) {
            throw new AuthenticationException("Username and password cannot be empty.");
        }

        String hashedPassword = PasswordHasher.hash(plainPassword);
        User user = userDAO.authenticate(username.trim(), hashedPassword);

        // Fallback for pre-seeded plain-text demo credentials in development/demo database
        if (user == null) {
            user = userDAO.authenticate(username.trim(), plainPassword);
        }

        if (user == null) {
            throw new AuthenticationException("Invalid username or password.");
        }

        SessionManager.setCurrentUser(user);
        return user;
    }

    public User login(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User details cannot be null.");
        }
        return login(user.getUsername(), user.getPassword());
    }

    public boolean register(Student student) throws ValidationException {
        if (student == null) {
            throw new ValidationException("Student details cannot be null.");
        }
        return register(student, student.getPassword());
    }

    public boolean register(Student student, String plainPassword) throws ValidationException {
        if (student == null) {
            throw new ValidationException("Student details cannot be null.");
        }

        if (!Validator.isNotEmpty(student.getUsername())) {
            throw new ValidationException("Username is required.");
        }

        if (!Validator.isNotEmpty(student.getFullName())) {
            throw new ValidationException("Full name is required.");
        }

        if (!Validator.isNotEmpty(student.getRollNumber())) {
            throw new ValidationException("Roll number is required.");
        }

        if (Validator.isNotEmpty(student.getEmail()) && !Validator.isEmailValid(student.getEmail())) {
            throw new ValidationException("Invalid email format.");
        }

        if (Validator.isNotEmpty(student.getPhone()) && !Validator.isPhoneValid(student.getPhone())) {
            throw new ValidationException("Invalid phone number format.");
        }

        String passwordToValidate = (plainPassword != null) ? plainPassword : student.getPassword();
        if (!Validator.isStrongPassword(passwordToValidate)) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }

        if (userDAO.usernameExists(student.getUsername().trim())) {
            throw new ValidationException("Username already exists. Please choose another username.");
        }

        student.setRole(Role.STUDENT);
        student.setUsername(student.getUsername().trim());
        student.setPassword(PasswordHasher.hash(passwordToValidate));

        return userDAO.registerUser(student);
    }
}
