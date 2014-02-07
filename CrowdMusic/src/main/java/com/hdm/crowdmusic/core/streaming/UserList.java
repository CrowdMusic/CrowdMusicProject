package com.hdm.crowdmusic.core.streaming;


import java.util.HashSet;
import java.util.Set;


public class UserList {

    Set<User> userlist;

    public UserList(){
        this.userlist = new HashSet<User>();
    }

    public Set<User>getUserList(){
        return this.userlist;
    }

    public void addUser(User newUser){
        userlist.add(newUser);
    }
}
