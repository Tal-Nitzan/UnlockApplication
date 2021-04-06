package com.example.unlockapplication;

import java.util.HashMap;
import java.util.Map;

public class DBMock {
    private Map<String, User> users;

    public DBMock() {
        this.users = new HashMap<>();
        addUsers();
    }

    private void addUsers() {
        this.users.put("tamir", new User("tamir", "1234"));
        this.users.put("tal", new User("tal", "1234"));
        this.users.put("guy", new User("guy", "1234"));
        this.users.put("tom", new User("tom", "1234"));
    }

    public User getUser(String username) {
        return this.users.get(username);
    }


}
