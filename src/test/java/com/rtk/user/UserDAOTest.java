package com.rtk.user;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import static com.rtk.config.Configuration.*;
import static org.assertj.core.api.Assertions.assertThat;

//user tests rely on data in the database, which I realise ids not best practice,
// but I needed to check if the methods actually do what they are supposed to
class UserDAOTest {
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
    }

    @Test
    void save_UserIsNull_ThrowsException() {
        //given
        User user = null;
        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.save(user));
    }
    @Test
    void save_ShouldSaveUserToDatabaseAndReturnValidUserWithId_whenPassedValidUserNotPresentInTheDatabase() throws SQLException {
        //given
        User user = User.builder()
                .username("user")
                .password("1234")
                .permission("user")
                .readOnly("no")
                .build();
        int userIdBeforeSaving = user.getUserid();
        //when
        User savedUser = userDAO.save(user);
        int userIdAfterSaving = savedUser.getUserid();
        //then
        assertThat(savedUser).isNotNull();
        assertThat(userIdBeforeSaving).isNotEqualTo(userIdAfterSaving);
    }
    @Test
    void save_ShouldThrowException_whenPassedUserPresentInDatabase(){
        //given
        User user = User.builder()
                .username("admin")
                .password("admin")
                .permission("superuser")
                .readOnly("yes")
                .build();
        Assertions.assertThrows(RuntimeException.class, () -> userDAO.save(user));
    }
    @Test
    void login_ShouldReturnEmptyOptional_WhenPassedInvalidUsername() throws SQLException {
        //when
        Optional<User> userOptional = userDAO.login("user", "admin");
        //then
        assertThat(userOptional).isEmpty();
    }
    @Test
    void login_ShouldReturnEmptyOptional_WhenPassedInvalidPassword() throws SQLException {
        //when
        Optional<User> userOptional = userDAO.login("admin", "1234");
        //then
        assertThat(userOptional).isEmpty();
    }

    @Test
    void login_ShouldNotReturnEmptyOptional_WhenPassedValidUsernameAndPassword() throws SQLException {
        //when
        Optional<User> userOptional = userDAO.login("admin", "admin");
        //then
        assertThat(userOptional).isNotEmpty();
    }
    @Test
    void login_ShouldSetValidUsersId_WhenPassedValidUsernameAndPasswordForUserWithReadOnlyEqualNo() throws SQLException {
        //when
        Optional<User> userOptional = userDAO.login("user", "1234");
        //then
        assertThat(UserDAO.validatedUsersId).isNotEqualTo(0);
    }

}