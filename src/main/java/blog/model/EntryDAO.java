package blog.model;

import exceptions.UserNotLoggedInException;
import lombok.RequiredArgsConstructor;
import utils.DatabaseCreator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class EntryDAO {
    //field for dependency injection
    private final Connection connection;
    List<Entry> findAll(){
        DatabaseCreator.createTables();
        //initiating a list to return, if no entries are found the method will just return it empty
        // instead of explicitly returning Collections.emptyList
        List<Entry> entries = new ArrayList<>();
        try (connection) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM blog");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                int userId = resultSet.getInt("userid");
                entries.add(new Entry(id, text, userId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }
    //I want to return the Blog instance passed to the method with id set same as in database in order to return a success message
    Entry save(Entry entry){
        if (entry == null) {
            throw new IllegalArgumentException("Failed to insert entry. Entry is null.");
        }
        //check if a user is logged in

        DatabaseCreator.createTables();
        try (connection) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO blog" +
                            "(text, userid) " +
                            "VALUES (?, ?, ?)");
            preparedStatement.setString(1, entry.getText());
            preparedStatement.setInt(2, UserDAO.validatedUsersId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT id FROM blog " +
                    "WHERE text=? AND userid=?");
            preparedStatement.setString(1, entry.getText());
            preparedStatement.setInt(2, entry.getUserid());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                entry.setId(id);
            }
            connection.commit();
            return entry;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //delete return type is String since I want to return a success message
    String delete(int id){
        DatabaseCreator.createTables();
        return null;
    }
}
