package utils;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static config.Configuration.*;

/**
 *I've considered creating the database schema with an SQL query and operating on a previously prepared schema,
 *but I've decided for creating a schema with Java so that it can be later recreated without the need to do it manually
 */
public class DatabaseCreator {
    public static void createDatabase(){
        //Using try with resources to close the connection on completion
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/", USER, PASSWORD);
            Statement statement = connection.createStatement()
        ){
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS blog_schema");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void createTables(){
        createDatabase(); //the idea is to first create the database schema and then connect to it and create tables
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ){
            connection.setAutoCommit(false);//Using transaction because I want to be sure the following queries execute in full

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS blog(" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "text  TEXT NOT NULL, " +
                    "userid INT NOT NULL, " +
                    "PRIMARY KEY(id)" +
                    ")");
            //adding a UNIQUE constraint on username and password since those fields should be unique in a user database
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS user(" +
                    "userid INT NOT NULL AUTO_INCREMENT, " +
                    "username VARCHAR(45) UNIQUE NOT NULL, " +
                    "password VARCHAR(45) UNIQUE NOT NULL, " +
                    "permission VARCHAR(45) NOT NULL, " +
                    "readonly VARCHAR(45) NOT NULL, " +
                    "PRIMARY KEY(userid)" +
                    ")");
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
