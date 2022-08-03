package com.rtk.user;

import com.rtk.utils.DatabaseCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserDAO {
    /*variable of keeping track of a logged-in user to pass their userid to newly created entries
     if the user has readonly set to no (I guess this is how it is supposed to work, this was not clarified),
    it is probably not industry standard but that is my idea of handling it*/
    public static int validatedUsersId;
    //field for dependency injection
    private final Connection connection;

    Optional<User> save(User user) throws SQLException {
        //although I am doing validation in the controller layer it is better to be safe than sorry and ensure
        // that an actual User object has been passed to the method,
        if (user == null) {
            throw new IllegalArgumentException("Failed to insert user. User is null.");
        }
        DatabaseCreator.createTables();
        //because I'm performing more than one database operation I want to be sure that both queries will be executed
        connection.setAutoCommit(false);
        //checking if user is already in the database
        if (isUserInDatabase(connection, user.getUsername(), user.getPassword())) {
            return Optional.empty();
        }
        PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT INTO user" +
                        "(username, password, permission, readonly) " +
                        "VALUES (?, ?, ?, ?)");
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setString(3, user.getPermission());
        preparedStatement.setString(4, user.getReadOnly());
        int rowsAffected = preparedStatement.executeUpdate();

        preparedStatement = connection.prepareStatement("SELECT userid FROM user " +
                "WHERE username=? AND password=?");
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int id = resultSet.getInt("userid");
            user.setUserid(id);
        }
        connection.commit();
        return Optional.ofNullable(user);//I prefer to be on the safe side when returning objects based on database
    }

    Optional<User> login(String username, String password) throws SQLException {
        DatabaseCreator.createTables();
        //I want to return full information on the user to be used on the frontend in case of a successful query
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user " +
                "WHERE username=? AND password=?");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        User user = null;
        if (resultSet.next()) {
            int userid = resultSet.getInt("userid");
            String name = resultSet.getString("username");
            String secret = resultSet.getString("password");
            String permission = resultSet.getString("permission");
            String readonly = resultSet.getString("readonly");
            user = User.builder()
                    .userid(userid)
                    .username(name)
                    .password(secret)
                    .permission(permission)
                    .readOnly(readonly)
                    .build();
            if ("no".equals(readonly)) {
                validatedUsersId = userid;
            }
        }
        return Optional.ofNullable(user);
    }

    boolean isUserInDatabase(Connection connection, String username, String password) throws SQLException {
        //I realise that with a large dataset this solution would take too long, but in such case I would expect
        // those columns would have an index set which would shorten the search time
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT username, password FROM user");

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("username");
            String secret = resultSet.getString("password");
            if (username.equals(name) && password.equals(secret)) {
                return true;
            }
        }
        return false;
    }
}

