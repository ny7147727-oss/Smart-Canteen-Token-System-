package com.smartcanteen.common.util;

import com.smartcanteen.model.User;

public class SessionManager {

    private static User currentUser;

    private SessionManager() {
        // Prevent instantiation
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
