package blog.model;

import config.Configuration;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import static config.Configuration.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

class UserDAOTest {
    Connection connection;
    UserDAO userDAO;

    @BeforeEach
    void setUp() {
        try {
            connection = DriverManager.getConnection(Configuration.URL, Configuration.USER, PASSWORD);
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
    void save_ShouldSaveUserToDatabaseAndReturnValidUserWithId_whenPassedValidUserNotPresentInTheDatabase(){
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
    void login_ShouldReturnEmptyOptional_WhenPassedInvalidUsername() {
        //when
        Optional<User> userOptional = userDAO.login("user", "admin");
        //then
        assertThat(userOptional).isEmpty();
    }
    @Test
    void login_ShouldReturnEmptyOptional_WhenPassedInvalidPassword() {
        //when
        Optional<User> userOptional = userDAO.login("admin", "1234");
        //then
        assertThat(userOptional).isEmpty();
    }

    @Test
    void login_ShouldNotReturnEmptyOptional_WhenPassedValidUsernameAndPassword() {
        //when
        Optional<User> userOptional = userDAO.login("admin", "admin");
        //then
        assertThat(userOptional).isNotEmpty();
    }
    @Test
    void login_ShouldSetValidUsersId_WhenPassedValidUsernameAndPasswordForUserWithReadOnlyEqualNo() {
        //when
        Optional<User> userOptional = userDAO.login("user", "1234");
        //then
        assertThat(UserDAO.validatedUsersId).isNotEqualTo(0);
    }

}