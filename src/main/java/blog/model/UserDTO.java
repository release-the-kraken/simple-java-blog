package blog.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO { //since the class is not a JPA entity, I treat it as a DTO
     private String userId;
     private String userName;
     private String password;
     private String permission;
     private String readOnly;
}
