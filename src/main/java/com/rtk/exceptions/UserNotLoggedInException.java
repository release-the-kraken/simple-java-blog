package com.rtk.exceptions;
//since I already implemented a mechanism for tracking  user log ins, I should use it to check if the user is logged in
// and allow blog operations only to logged-in users
public class UserNotLoggedInException extends RuntimeException{
    public UserNotLoggedInException(String message) {
        super(message);
    }
}
