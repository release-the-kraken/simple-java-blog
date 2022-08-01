package com.rtk.entry;

import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import com.rtk.user.UserDAO;
import lombok.RequiredArgsConstructor;
import com.rtk.utils.DatabaseCreator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EntryDAO {
    //field for dependency injection
    private final Connection connection;

    List<Entry> findAll() {
        DatabaseCreator.createTables();
        //initiating a list to return, if no entries are found the method will just return it empty
        List<Entry> entries = new ArrayList<>();
        try (connection) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM blog");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                int userId = resultSet.getInt("userid");
                entries.add(new Entry(id, text, userId));
            }
        } catch (SQLException e) {
            log.info("SQL Exception: " + e.getMessage());
            return Collections.emptyList();
        }
        return entries;
    }

    //I'm only passing one argument which is the entry text, this will be passed to the database along with user id,
    //entry will be retrieved with set id and used to create an Entry object to return to the frontend
    Entry save(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Failed to insert entry. Text cannot be empty.");
        }

        DatabaseCreator.createTables();
        try (connection) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO blog" +
                            "(text, userid) " +
                            "VALUES (?, ?)");
            preparedStatement.setString(1, text);
            preparedStatement.setInt(2, UserDAO.validatedUsersId);
            preparedStatement.executeUpdate();

            preparedStatement = connection
                    .prepareStatement("SELECT * FROM blog " +
                    "WHERE text=? AND userid=?");
            preparedStatement.setString(1, text);
            preparedStatement.setInt(2, UserDAO.validatedUsersId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Entry savedEntry = new Entry();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String entryText = resultSet.getString("text");
                int userId = resultSet.getInt("userid");
                savedEntry.setId(id);
                savedEntry.setText(entryText);
                savedEntry.setUserid(userId);
            }
            connection.commit();
            return savedEntry;
        } catch (SQLException e) {
            log.info("SQL Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //returning a success message which will be converted to json in the controller layer
    String delete(int id) {
        DatabaseCreator.createTables();

        try (connection) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM blog WHERE id=?");
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();//executeUpdate returns number of table rows affected
            return rowsAffected > 0 //which I'm using to display success or failure message
                    ? "Safely deleted entry " + id
                    : "No entry with id " + id;

        } catch (SQLException e) {
            log.info("SQL Exception: " + e.getMessage());
            return e.getMessage();
        }
    }
}
