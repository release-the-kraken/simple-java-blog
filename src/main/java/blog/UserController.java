package blog;

import blog.model.User;
import blog.model.UserDAO;
import com.google.gson.Gson;
import exceptions.UserDataValidationException;
import lombok.RequiredArgsConstructor;


/*I've decided for a two tier architecture since there is no actual business logic like sorting or entity<->DTO mapping
that goes into the service layer*/
@RequiredArgsConstructor
public class UserController {
    //fields for dependency injection
    private final UserDAO userDAO;
    Gson gson;
/*   I don't have to openly declare that the method throws an IllegalArgumentException,
      but I want to inform any future users of this method of that fact*/
    public String login(String username, String password) throws IllegalArgumentException, UserDataValidationException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return userDAO.login(username, password)
                .map(user -> gson.toJson(user))
                .orElseThrow(() -> new UserDataValidationException("Invalid username or password"));
    }
    //returning a String to inform the client of success
    public String addUser(String username, String password, String permission, String readonly) throws IllegalArgumentException {
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
        //using the builder pattern for a cleaner look
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
