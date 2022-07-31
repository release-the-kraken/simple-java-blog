package blog;

import blog.model.BlogDAO;
import blog.model.UserDAO;
import lombok.RequiredArgsConstructor;
//constructor for final fields, lombok helps in focusing on a classes actual task, without the visual clutter of object methods
@RequiredArgsConstructor
public class BlogController {
    //field for dependency injection
    private final BlogDAO blogDAO;

//    methods are public to allow access from classes which actually perform http request/response processing
    public String listAllEntries(){

        return null;
    }
    //returning a String to inform the client of success or failure
    public String addEntry(String text){

        return null;
    }
    //returning a String to inform the client of success or failure
    public String deleteEntry(int id){

        return null;
    }


}
