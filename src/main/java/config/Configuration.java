package config;

public class Configuration {
    public static final String URL= "jdbc:mysql://localhost/blog_schema";
    public static final String USER = "admin";
    public static final String PASSWORD =  "admin";
    private Configuration() {
    //I don't want any instances of the class created
    }
}
