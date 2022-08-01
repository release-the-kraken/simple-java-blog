package blog;

import blog.model.EntryDAO;
import blog.model.UserDAO;
import com.google.gson.Gson;
import exceptions.UserNotLoggedInException;
import lombok.RequiredArgsConstructor;
//constructor for final fields, lombok helps in focusing on a classes actual task, without the visual clutter of object methods
@RequiredArgsConstructor
public class EntryController {
    //fields for dependency injection
    private final EntryDAO blogDAO;
    private final Gson gson;
//    methods are public to allow access from classes which actually perform http request/response processing
    public String listAllEntries(){

        return null;
    }
    //returning a String to inform the client of success or failure
    public String addEntry(String text){
        if(UserDAO.validatedUsersId == 0){
            throw new UserNotLoggedInException("Adding entries only possible when user is logged in.");
        }
        if(text == null || text.isBlank()){
            throw new IllegalArgumentException("Text cannot be empty.");
        }

        return null;
    }
    //returning a String to inform the client of success or failure
    public String deleteEntry(int id){

        return null;
    }


}
