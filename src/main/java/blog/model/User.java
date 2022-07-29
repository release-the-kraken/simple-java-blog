package blog.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
     private String userId;
     private String userName;
     private String password;
     private String permission;
     private String readOnly;
}
