package com.example.minimusicplayer.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQL extends SQLiteOpenHelper {

    public SQL(Context context) {
        super(context, "MusicList.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Music(" +
                "id integer primary key," +
                "name text," +
                "singer text," +
                "path text," +
                "wordpath text)");
        db.execSQL("create table List(" +
                "id integer primary key," +
                "name text)");
        db.execSQL("create table Link(" +
                "music integer," +
                "list integer," +
                "primary key(music,list))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
