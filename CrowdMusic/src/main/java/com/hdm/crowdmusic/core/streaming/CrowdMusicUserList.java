package com.hdm.crowdmusic.core.streaming;


import java.util.ArrayList;
import java.util.List;


public class CrowdMusicUserList {

    List<CrowdMusicUser> userlist;

    public CrowdMusicUserList(){
        this.userlist = new ArrayList<CrowdMusicUser>();
    }

    public List<CrowdMusicUser>getUserList(){
        return this.userlist;
    }

    public void addUser(CrowdMusicUser newUser){

        for(CrowdMusicUser user : userlist){
            if (user.getIp() == newUser.getIp()){
                return;
            }
        }
        userlist.add(newUser);
    }
}
