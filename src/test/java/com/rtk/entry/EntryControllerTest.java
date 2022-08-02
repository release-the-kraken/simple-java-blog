package com.rtk.entry;

import com.google.gson.Gson;
import com.rtk.exceptions.UserNotLoggedInException;
import com.rtk.user.UserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.rtk.config.Configuration.*;
import static org.assertj.core.api.Assertions.*;

class EntryControllerTest {
    EntryController entryController;
    Connection connection;
    EntryDAO entryDAO;

    @BeforeEach
    void setUp() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        entryDAO = new EntryDAO(connection);
        entryController = new EntryController(entryDAO, new Gson());
    }

    @Test
    void listAllEntries_ShouldReturnArray() {
        //given
        String type = entryController
                .listAllEntries();
        //then
        assertThat(entryController.listAllEntries()).isEqualTo("[]");
    }

    @Test
    void addEntry_ShouldThrowException_whenUserIsNotLoggedIn() {
        //given
        UserDAO.validatedUsersId = -1; //setting up the variable to perform tests without the need to actually log in

        //then
        Assertions.assertThrows(UserNotLoggedInException.class, () -> entryController.addEntry("Lorem ipsum"));
    }

    @Test
    void deleteEntry_ShouldThrowException_WhenPassedIdLessThan1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> entryController.deleteEntry(0));
    }

}