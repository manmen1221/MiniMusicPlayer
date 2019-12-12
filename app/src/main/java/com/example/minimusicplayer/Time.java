package com.example.minimusicplayer;

import android.os.Handler;

public class Time implements Runnable {
    private MainActivity mainActivity;
    private Handler handler=new Handler();
    public Time(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }
    public void run() {
        try {
            mainActivity.refreshPlayer();
            handler.postDelayed(this, 10);
            try{
                mainActivity.setWord(mainActivity.getMusicPlayer().getWord().getWord(mainActivity.getMusicPlayer().player.getCurrentPosition()));
            }catch(Exception e){ }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
