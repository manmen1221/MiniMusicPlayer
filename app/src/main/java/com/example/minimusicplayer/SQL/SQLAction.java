package com.example.minimusicplayer.SQL;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.minimusicplayer.MusicPlayer;
import com.example.minimusicplayer.container.Group;
import com.example.minimusicplayer.container.Music;

import java.util.List;

public class SQLAction {
    public static SQLiteDatabase sql;
    public static void read(MusicPlayer mp){
        List<Music> list=mp.getData().getMainList();
        List<Group> gList=mp.getData().getGroupList();

        Cursor cursor=sql.rawQuery("select * from Music",null);
        while(cursor.moveToNext()){
            Music music=new Music();
            music.id=cursor.getInt((cursor.getColumnIndex("id")));
            music.name=cursor.getString(cursor.getColumnIndex("name"));
            music.singer=cursor.getString(cursor.getColumnIndex("singer"));
            music.path=cursor.getString(cursor.getColumnIndex("path"));
            music.wordPath=cursor.getString(cursor.getColumnIndex("wordpath"));
            list.add(music);
        }
        cursor.close();
        cursor=sql.rawQuery("select * from List",null);
        while (cursor.moveToNext()){
            Group group=new Group();
            group.id=cursor.getInt((cursor.getColumnIndex("id")));
            group.name=cursor.getString(cursor.getColumnIndex("name"));
            gList.add(group);
        }
        cursor.close();
        cursor=sql.rawQuery("select * from Link",null);
        while (cursor.moveToNext()){
            int m,g;
            m=cursor.getInt((cursor.getColumnIndex("music")));
            g=cursor.getInt((cursor.getColumnIndex("list")));
            Music music=list.get(m);
            Group group=gList.get(g);
            group.getList().add(music);
        }
        cursor.close();
    }


    public static void addGroup(int id,String name){
        sql.execSQL("insert into List (id,name) values("+id+",'"+name+"')");
    }
    public static void addMusic(int id,String name,String singer,String path,String word){
        sql.execSQL("insert into Music (id,name,singer,path,wordpath) values("+id+",'"+name+"','"+singer+"','"+path+"','"+word+"')");
    }
    public static void saveMusic(int id,String name,String singer,String path,String word){
        sql.execSQL("update Music set name='"+name+"',singer='"+singer+"',path='"+path+"',wordpath='"+word+"' where id="+id);
    }
    public static void saveGroup(int id,String name){
        sql.execSQL("update List set name='"+name+"' where id="+id);
    }
    public static void removeGroup(int id){
        sql.execSQL("delete from List where id="+id);
        sql.execSQL("delete from Link where list="+id);
        sql.execSQL("update List set id=id-1 where id>"+id);
        sql.execSQL("update Link set list=list-1 where list>"+id);
    }
    public static void removeMusic(int id){
        sql.execSQL("delete from Music where id="+id);
        sql.execSQL("delete from Link where music="+id);
        sql.execSQL("update Link set music=music-1 where music>"+id);
    }
    public static void addToGroup(int music,int group){
        sql.execSQL("insert into Link (music,list) values("+music+","+group+")");
    }
    public static void removeFromGroup(int music,int group){
        sql.execSQL("delete from Link where music="+music+" and list="+group);
    }
}
