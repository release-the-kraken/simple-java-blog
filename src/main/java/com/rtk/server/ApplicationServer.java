package com.rtk.server;

import com.google.gson.Gson;
import com.rtk.entry.EntryController;
import com.rtk.entry.EntryDAO;
import com.rtk.user.UserController;
import com.rtk.user.UserDAO;
import com.rtk.utils.RequestURIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;

import static com.rtk.config.Configuration.*;
import static com.rtk.config.Configuration.PASSWORD;
import static com.rtk.config.Configuration.USER;

//the logic of the app is that you should have a valid user logged-in in order to add new entries
@Slf4j
public class ApplicationServer {
    public static void main(String[] args) {
        Connection connection;
        {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                log.info("SQL Exception: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        EntryDAO entryDAO = new EntryDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        Gson gson = new Gson();
        EntryController entryController = new EntryController(entryDAO, gson);
        UserController userController = new UserController(userDAO, gson);
        try {
            int port = 8000;
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
            server.createContext("/blog", (exchange -> {
                //parsing request URI to extract action name
                OutputStream outputStream = exchange.getResponseBody();
                String requestURI = exchange.getRequestURI().toString();
                String action = RequestURIHelper.getActionFromRequestString(requestURI);
                String responseText;
                String errorMessage;
                //assigning behavior based on request action
                switch (action) {
                    case "login":
                        loginUser(userController, exchange, outputStream, requestURI);
                        break;
                    case "new":
                        addNewEntry(entryController, exchange, outputStream, requestURI);
                        break;
                    case "new_user":
                        addNewUser(userController, exchange, outputStream, requestURI);
                        break;
                    case "delete":
                        deleteEntry(entryController, exchange, outputStream, requestURI);
                        break;

                    case "":
                        listEntries(entryController, exchange, outputStream);
                        break;
                    default:
                        exchange.sendResponseHeaders(404, 0);
                }
                outputStream.flush();
                exchange.close();
            }));
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            log.info(String.format("Server listening on port %s.", port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void listEntries(EntryController entryController, HttpExchange exchange, OutputStream outputStream) throws IOException {
        String errorMessage;
        String responseText;
        try {
            responseText = entryController.listAllEntries();
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            outputStream.write(responseText.getBytes());
        } catch (SQLException e) {
            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
            outputStream.write(errorMessage.getBytes());
        }
    }

    private static void deleteEntry(EntryController entryController, HttpExchange exchange, OutputStream outputStream, String requestURI) throws IOException {
        String responseText;
        String errorMessage;
        Map<String, String> requestParameters = RequestURIHelper.getParameters(requestURI);
        int id = Integer.parseInt(requestParameters.get("id"));
        try {
            responseText = entryController.deleteEntry(id);
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            outputStream.write(responseText.getBytes());
        } catch (SQLException e) {
            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
            outputStream.write(errorMessage.getBytes());
        }
    }

    private static void addNewUser(UserController userController, HttpExchange exchange, OutputStream outputStream, String requestURI) throws IOException {
        String errorMessage;
        String responseText;
        Map<String, String> requestParameters = RequestURIHelper.getParameters(requestURI);
        String username = requestParameters.get("username");
        String password =requestParameters.get("password");
        String permission = requestParameters.get("permission");
        String readOnly =requestParameters.get("readonly");
        try {
            responseText = userController.addUser(username, password, permission, readOnly);
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            outputStream.write(responseText.getBytes());
        } catch (SQLException e) {
            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
            outputStream.write(errorMessage.getBytes());
        }
    }

    private static void addNewEntry(EntryController entryController, HttpExchange exchange, OutputStream outputStream, String requestURI) throws IOException {
        String errorMessage;
        String responseText;
        Map<String, String> requestParameters = RequestURIHelper.getParameters(requestURI);
        String text = requestParameters.get("text");
        try {
            responseText = entryController.addEntry(text);
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            outputStream.write(responseText.getBytes());
        } catch (SQLException e) {
            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
            outputStream.write(errorMessage.getBytes());
        }
    }

    private static void loginUser(UserController userController, HttpExchange exchange, OutputStream outputStream, String requestURI) throws IOException {
        String responseText;
        String errorMessage;
        Map<String, String> requestParameters = RequestURIHelper.getParameters(requestURI);
        String username = requestParameters.get("user");
        String password =requestParameters.get("password");
        try {
            responseText = userController.login(username, password);
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            outputStream.write(responseText.getBytes());
        } catch (SQLException e) {
            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
            outputStream.write(errorMessage.getBytes());
        }
    }


}
