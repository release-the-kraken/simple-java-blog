package com.rtk.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestURIHelper {
    //no instances of class should be created
    private RequestURIHelper() {
    }

    public static String getActionFromRequestString(String requestURI) {
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
    public static Map<String, String> getParameters(String requestURI) {
        if (requestURI == null || requestURI.isBlank()) {
            throw new IllegalArgumentException("Request URI cannot be blank.");
        }
        Map<String, String> parameters = new HashMap<>();
        //extracting request parameters string from request URI
        String parametersFromURI = requestURI.substring(requestURI.indexOf("&") + 1);
        //mapping parameter pairs (key=value) to list
        List<String> parameterPairs = List.of(parametersFromURI.split("&"));
        //mapping parameter pairs list to map
        parameterPairs.forEach(pair -> toHashMap(parameters, pair));
        return parameters;
    }

    private static void toHashMap(Map<String, String> parameters, String pair) {
        //cutting out the part before '=' for map key
        String key = pair.substring(0, pair.indexOf("="));
        //cutting out the part after '=' for map value
        String value = pair.substring(pair.indexOf("=") + 1);
        parameters.put(key, value);
    }
}
