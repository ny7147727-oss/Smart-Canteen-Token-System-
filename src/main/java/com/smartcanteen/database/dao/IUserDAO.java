package com.smartcanteen.database.dao;

import com.smartcanteen.model.User;

public interface IUserDAO {
    User authenticate(String username, String hashedPassword);
    User authenticate(User user);
    boolean registerUser(User user);
    boolean usernameExists(String username);
    User getUserById(int id);
}
