package blog.model;

import blog.model.Blog;

import java.util.Collections;
import java.util.List;

public class BlogDAO {
    List<Blog> findAll(){

        return Collections.emptyList();
    }
    //I want to return the Blog instance passed to the method with id set same as in database in order to return a success message
    Blog save(Blog blog){

        return null;
    }
    //delete return type is String since I want to return a success message
    String delete(int id){

        return null;
    }
}
