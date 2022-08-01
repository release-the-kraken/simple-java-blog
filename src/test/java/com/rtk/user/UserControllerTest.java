package com.rtk.user;

import com.google.gson.Gson;
import com.rtk.entry.EntryController;
import com.rtk.entry.EntryDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.rtk.config.Configuration.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;
    Connection connection;
    UserDAO userDAO;

    @BeforeEach
    void setUp() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userDAO = new UserDAO(connection);
        userController = new UserController(userDAO, new Gson());
    }
    @Test
    void login_ShouldThrowException_WhenPassedEmptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.login(null, "admin"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.login("   ", "admin"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.login("admin", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.login("admin", "   "));
    }

    @Test
    void addUser_ShouldThrowException_WhenPassedEmptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser(null, "a", "b", "c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("   ", "a", "b", "c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("a", null, "b", "c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("a", "   ", "b", "c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("a", "a", null, "c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("a", "a", "   ", "c"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("a", "a", "b", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userController.addUser("a", "a", "b", "   "));
    }
}