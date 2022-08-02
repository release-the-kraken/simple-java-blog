package com.rtk.user;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;


/*I've decided for two tier architecture since there is no actual business logic like sorting or entity<->DTO mapping
that goes into the service layer*/
@RequiredArgsConstructor
public class UserController {
    //fields for dependency injection
    private final UserDAO userDAO;
    private final Gson gson;
/*   I don't have to openly declare that the method throws an IllegalArgumentException,
      but I want to inform any future users of this method of that fact*/
    public String login(String username, String password) throws IllegalArgumentException, SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String resp = userDAO.login(username, password)
                .map(user -> gson.toJson(user))
                .orElseGet(() -> "{\"error_message\":\"Invalid username or password.\"}");
        System.out.println(resp);
        return resp;
    }
    //returning a String to inform the client of success
    public String addUser(String username, String password, String permission, String readonly) throws IllegalArgumentException, SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("Field \"permission\" cannot be empty");
        }
        if (readonly == null || readonly.isBlank()) {
            throw new IllegalArgumentException("Field \"readonly\" cannot be empty");
        }
        //using the builder pattern for cleaner code
        User user = User.builder()
                .username(username)
                .password(password)
                .permission(permission)
                .readOnly(readonly)
                .build();
        User savedUser = userDAO.save(user);
        return gson.toJson(savedUser);
    }
}
