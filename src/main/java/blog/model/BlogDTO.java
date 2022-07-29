package blog.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogDTO { //since the class is not a JPA entity, I treat it as a DTO
    private int id;
    private String text;
    private int userid;
}
