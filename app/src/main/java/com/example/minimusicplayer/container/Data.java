package com.example.minimusicplayer.container;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Music> mainList=new ArrayList<Music>();
    private List<Group> groupList=new ArrayList<Group>();
    public List<Music> getMainList(){
        return mainList;
    }
    public List<Group> getGroupList(){
        return groupList;
    }
}
