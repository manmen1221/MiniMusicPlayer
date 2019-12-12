package com.example.minimusicplayer.container;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public boolean show=false;
    public int id;
    public String name;
    private List<Music> list=new ArrayList<Music>();
    public List<Music> getList(){
        return list;
    }
    public int size(){
        return list.size();
    }
}
