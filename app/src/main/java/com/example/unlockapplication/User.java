package com.example.unlockapplication;

public class User {
    private String username;
    private String password;
    private int numOfAttempts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        resetLoginAttempts();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getNumOfAttempts() {
        return numOfAttempts;
    }

    public void addLoginAttempt() {
        ++this.numOfAttempts;
    }

    public void resetLoginAttempts() {
        this.numOfAttempts = 0;
    }
}
