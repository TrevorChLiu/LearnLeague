package edu.cuhk.csci3310.learnleague;

public class User {
    public User(String userID, String password) {
        ApiClient.createUser(userID, password.hashCode());
    }
}
