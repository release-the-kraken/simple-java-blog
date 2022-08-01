package com.rtk.exceptions;
//I've chosen for this class to extend Exception (checked) because I think this should actually require taking care of
public class UserDataValidationException extends Exception{
    public UserDataValidationException(String message) {
        super(message);
    }
}
