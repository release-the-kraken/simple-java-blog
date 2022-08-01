package com.rtk.entry;

import com.rtk.user.UserDAO;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.rtk.config.Configuration.*;
import static org.assertj.core.api.Assertions.assertThat;

class EntryDAOTest {
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

    }
    @Test
    void findAll_ShouldReturnEmptyList_BeforeAnyEntryIsSaved() {
        //when
        List<Entry> entries = entryDAO.findAll();
        //then
        assertThat(entries).isEmpty();
    }
    @Test
    void save_ShouldThrowException_WhenPassedEmptyString() {
        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> entryDAO.save(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> entryDAO.save("  "));
    }
    @Test
    void save_ShouldSaveTextToDatabaseAndReturnValidEntryWithIdAndUserId_whenPassedValidText(){
        //given
        UserDAO.validatedUsersId = 5; //setting up the variable to perform tests without the need to actually log in
        Entry entry = new Entry();
        int entryIdBeforeSaving = entry.getId();
        //when
        Entry savedEntry = entryDAO.save("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        int entryIdAfterSaving = savedEntry.getId();
        //then
        assertThat(savedEntry).isNotNull();
        assertThat(entryIdBeforeSaving).isNotEqualTo(entryIdAfterSaving);
    }
    @Test
    void findAll_ShouldReturnEntryList_AfterEntryIsSaved() {
        //when
        List<Entry> entries = entryDAO.findAll();
        //then
        assertThat(entries).isNotEmpty();
    }

    @Test
    void delete_ShouldReturnSuccessMessage_WhenPassedValidId() {
        //given
        String expected = "Safely deleted entry 4";
        //when
        String result = entryDAO.delete(4);
        //then
        assertThat(result).isEqualTo(expected);
    }
    @Test
    void delete_ShouldReturnFailureMessage_WhenPassedInvalidId() {
        //given
        String expected = "No entry with id 4";
        //when
        String result = entryDAO.delete(4);
        //then
        assertThat(result).isEqualTo(expected);
    }

}