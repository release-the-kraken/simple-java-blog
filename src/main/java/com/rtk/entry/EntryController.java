package com.rtk.entry;

import com.rtk.user.UserDAO;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
/*I've decided for two tier architecture since there is no actual business logic like sorting or entity<->DTO mapping
that goes into the service layer*/
public class EntryController {
    //fields for dependency injection
    private final EntryDAO entryDAO;
    private final Gson gson;
//    methods are public to allow access from classes which actually perform http request/response processing
    public String listAllEntries() throws SQLException {
        //method creates a json objects array to return to the frontend
        return entryDAO.findAll()
                .stream()
                .map(entry -> gson.toJson(entry))//I find lambdas more readable than method references
                .collect(Collectors.joining(",", "[", "]"));
    }
    //returning a json String to frontend
    public String addEntry(String text) throws SQLException {
        //check if user is logged in
        if(UserDAO.validatedUsersId <= 0){
            return "{\"error_message\":\"Adding entries only possible when user is logged in.\"}";
        }
        if(text == null || text.isBlank()){
            throw new IllegalArgumentException("Text cannot be empty.");
        }
        Entry savedEntry = entryDAO.save(text);
        return gson.toJson(savedEntry);
    }
    //returning a json String to inform the client of success or failure
    public String deleteEntry(int id) throws SQLException {
        if(id < 1){
            throw new IllegalArgumentException("Id cannot be less than 1");
        }
        String message = entryDAO.delete(id);
        return String.format("{\"message\":\"%s\"}", message);
    }


}
