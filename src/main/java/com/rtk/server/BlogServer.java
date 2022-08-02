package com.rtk.server;

import com.google.gson.Gson;
import com.rtk.entry.EntryController;
import com.rtk.entry.EntryDAO;
import com.rtk.user.UserController;
import com.rtk.user.UserDAO;
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
import java.util.stream.Collectors;

import static com.rtk.config.Configuration.*;
import static com.rtk.config.Configuration.PASSWORD;
import static com.rtk.config.Configuration.USER;

//the logic of the app is that you should have a valid user logged in order to add new entries
@Slf4j
public class BlogServer {
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
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
            server.createContext("/blog", (exchange -> {
                //parsing request URI to extract action name
                OutputStream outputStream = exchange.getResponseBody();
                String requestURI = exchange.getRequestURI().toString();
                String action = getActionFromRequestString(requestURI);
                String responseText;
                String errorMessage;
                //assigning behavior based on request action
                switch (action) {
                    case "login":
                        getParameters(requestURI);
                        try {
                            responseText = userController.login("admin", "amin");
                            exchange.sendResponseHeaders(200, responseText.getBytes().length);
                            outputStream.write(responseText.getBytes());
                        }  catch (SQLException e) {
                            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
                            outputStream.write(errorMessage.getBytes());
                        }
                        break;
                    case "new":
                        try {
                            responseText = entryController.addEntry("Lorem ipsum dolor");
                            exchange.sendResponseHeaders(200, responseText.getBytes().length);
                            outputStream.write(responseText.getBytes());
                        } catch (SQLException e) {
                            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
                            outputStream.write(errorMessage.getBytes());
                        }
                        break;
                    case "new_user":
                        try {
                            responseText = userController.addUser("a", "b", "user2", "no");
                            exchange.sendResponseHeaders(200, responseText.getBytes().length);
                            outputStream.write(responseText.getBytes());
                        } catch (SQLException e) {
                            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
                            outputStream.write(errorMessage.getBytes());
                        }
                        break;
                    case "delete":
                        try {
                            responseText = entryController.deleteEntry(10);
                            exchange.sendResponseHeaders(200, responseText.getBytes().length);
                            outputStream.write(responseText.getBytes());
                        } catch (SQLException e) {
                            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
                            outputStream.write(errorMessage.getBytes());
                        }
                        break;

                    case "":
                        try {
                            responseText = entryController.listAllEntries();
                            exchange.sendResponseHeaders(200, responseText.getBytes().length);
                            outputStream.write(responseText.getBytes());
                        } catch (SQLException e) {
                            errorMessage = String.format("{\"error_message\":\"%s\"}", e.getMessage());
                            outputStream.write(errorMessage.getBytes());
                        }
                        break;
                }
                outputStream.flush();
                exchange.close();
            }));
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getActionFromRequestString(String requestURI) {
        if (requestURI == null || requestURI.isBlank()) {
            throw new IllegalArgumentException("Request URI cannot be blank.");
        }
        // extracting the string between first '=' symbol and first '&' symbol, for the following URI
        // /blog?action=new_user&username=test&password=test&permission=superuser&readonly=yes
        // the returned value will be "new_user"
        return requestURI.equals("/blog")
                ? "" : requestURI.substring(requestURI.indexOf("=") + 1, requestURI.indexOf("&"));
    }
    //parsing requestURI to extract parameters into key, value pairs
    private static Map<String, String> getParameters(String requestURI) {
        if (requestURI == null || requestURI.isBlank()) {
            throw new IllegalArgumentException("Request URI cannot be blank.");
        }
        String parametersFromURI = requestURI.substring(requestURI.indexOf("&") + 1);
        List<String> parameterPairs = List.of(parametersFromURI.split("&"));

        log.info(parameterPairs.toString());


        return Collections.emptyMap();
    }
}
