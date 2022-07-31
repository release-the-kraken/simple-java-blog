package blog.model;

import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDAO {
    //field for dependency injection
    private final Connection connection;

    public User save(User user) throws RuntimeException {
        //although I am doing validation in the controller layer it is better to be safe than sorry and ensure
        // that an actual User object has been passed to the method,
        if (user == null) {
            throw new IllegalArgumentException("Failed to insert user. User is null.");
        }
        try (connection) {
            //because I'm performing more than one database operation I want to be sure that both queries will be executed
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO user" +
                            "(username, password, permission, readonly) " +
                            "VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getPermission());
            preparedStatement.setString(4, user.getReadOnly());
            preparedStatement.executeUpdate();

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
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> login(String username, String password) {
        try (connection) {
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
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

}

