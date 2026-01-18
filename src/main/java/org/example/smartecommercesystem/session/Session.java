package org.example.smartecommercesystem.session;

import org.example.smartecommercesystem.model.User;

public class Session {

    private static Session instance;
    private User currentUser;

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }


    public boolean isAdmin() {
        if (currentUser == null) return false;

        // OPTION 1: role-based
        try {
            return "Admin".equalsIgnoreCase(currentUser.getRole());
        } catch (Exception ignored) {
        }

        // OPTION 2: boolean-based
        try {
            return currentUser.isAdmin();
        } catch (Exception ignored) {
        }

        return false;
    }
}
