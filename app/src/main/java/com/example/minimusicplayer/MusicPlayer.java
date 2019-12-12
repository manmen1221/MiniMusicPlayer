package com.example.minimusicplayer;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.minimusicplayer.SQL.SQL;
import com.example.minimusicplayer.SQL.SQLAction;
import com.example.minimusicplayer.container.Data;
import com.example.minimusicplayer.container.Group;
import com.example.minimusicplayer.container.Music;
import com.example.minimusicplayer.container.Word;

import java.io.File;
import java.util.List;


public class MusicPlayer {
    private static MusicPlayer musicPlayer;
    private static int i=0;
    private MusicPlayer(){}
    public static MusicPlayer getMusicPlayer(Context context){
        if(i==0){
            i++;
            musicPlayer=new MusicPlayer();
            readData(context);
            musicPlayer.player.reset();
            return musicPlayer;
        }else if(i==1&&musicPlayer!=null)return musicPlayer;
        return null;
    }
    public static MusicPlayer musicPlayer(){
        return musicPlayer;
    }
    private Data data=new Data();
    public Data getData(){return data;}
    private static void readData(Context context){
        SQLAction.sql=new SQL(context).getWritableDatabase();
        SQLAction.read(musicPlayer);
    }

    public void addMusic(String name,String singer,String path,String word){
        int id=data.getMainList().size();
        Music music=new Music();
        music.id=id;
        music.name=name;
        music.singer=singer;
        music.path=path;
        music.wordPath=word;
        data.getMainList().add(music);
        SQLAction.addMusic(id,name,singer,path,word);
    }
    public void saveMusic(int id,String name,String singer,String path,String word){
        Music music=data.getMainList().get(id);
        music.name=name;
        music.singer=singer;
        music.path=path;
        music.wordPath=word;
        SQLAction.saveMusic(id,name,singer,path,word);
    }
    public void addGroup(String name){
        int id=data.getGroupList().size();
        Group group=new Group();
        group.id=id;
        group.name=name;
        data.getGroupList().add(group);
        SQLAction.addGroup(id,name);
    }
    public void saveGroup(int id,String name){
        Group group=data.getGroupList().get(id);
        group.name=name;
        SQLAction.saveGroup(id,name);
    }
    public void removeMusic(int id){
        List<Music> mlist=data.getMainList();
        List<Group> glist=data.getGroupList();
        Music remove=mlist.remove(id);
        for(int j=id;j<mlist.size();j++){
            mlist.get(j).id-=1;
        }
        for(int j=0;j<glist.size();j++){
            glist.get(j).getList().remove(remove);
        }
        SQLAction.removeMusic(id);
    }
    public void removeGroup(int id){
        List<Group> list=data.getGroupList();
        list.remove(id);
        for(int j=id;j<list.size();j++){
            list.get(j).id-=1;
        }
        SQLAction.removeGroup(id);
    }
    public boolean addToGroup(int music,int group){
        Music m=data.getMainList().get(music);
        Group g=data.getGroupList().get(group);
        if(g.getList().contains(m))return false;
        g.getList().add(m);
        SQLAction.addToGroup(music,group);
        return true;
    }
    public void removeFromGroup(int music,int group){
        Music m=data.getMainList().get(music);
        Group g=data.getGroupList().get(group);
        g.getList().remove(m);
        SQLAction.removeFromGroup(music,group);
    }



////////////////////↑数据操作/////↓音乐播放///////////////////////////////


    private Word word;
    public Word getWord(){
        return  word;
    }
    public MediaPlayer player=new MediaPlayer();
    private List<Music> selected=data.getMainList();
    private int selectedId=-1;
    public void setSelectedId(int id){
        this.selectedId=id;
    }
    public int getSelectedId(){
        return selectedId;
    }
    public List<Music> getSelected(){return selected;}
    private int musicId=-1;
    public int getMusicId(){
        return musicId;
    }

    public boolean playing(){
        return player.isPlaying();
    }


    public void play(int i){//播放第几首
        // TODO: 2019/12/5
        musicId=i;
        if(i==-1){
            word=new Word("");
            return;
        }
        Music music=data.getMainList().get(i);
        try{
            player.reset();
            File file=new File(music.path);
            player.setDataSource(file.getPath());
            player.prepare();
            word=new Word(music.wordPath);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public void play(){//播放
        player.start();
    }
    public void pause(){//暂停
        player.pause();
    }
    public void selectGroup(int i){//切换歌单
        if(i==-1)selected=data.getMainList();
        else selected=data.getGroupList().get(i).getList();
        selectedId=i;
    }
    public void changeBar(int time){//改进度条
        player.seekTo(time);
    }
}
