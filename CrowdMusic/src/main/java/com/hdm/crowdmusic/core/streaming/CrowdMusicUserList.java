package com.hdm.crowdmusic.core.streaming;


import java.util.HashSet;
import java.util.Set;


public class CrowdMusicUserList {

    Set<CrowdMusicUser> userlist;

    public CrowdMusicUserList(){
        this.userlist = new HashSet<CrowdMusicUser>();
    }

    public Set<CrowdMusicUser>getUserList(){
        return this.userlist;
    }

    public void addUser(CrowdMusicUser newUser){
        userlist.add(newUser);
    }
}
