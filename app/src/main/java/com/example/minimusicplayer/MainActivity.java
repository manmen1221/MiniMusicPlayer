package com.example.minimusicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.minimusicplayer.Tool.UriUtils;
import com.example.minimusicplayer.container.Group;
import com.example.minimusicplayer.container.Music;
import com.example.minimusicplayer.container.Tag;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText pathTemp;
    MusicPlayer musicPlayer;
    Button listBt,infoBt,singleBt,groupBt,playerInfo,playerS,playerX,playerB,playerG,addGroup,addMusic;
    LinearLayout listL,infoL,singleL,groupL,singleSv,groupSv;
    TextView infoNameTv,infoSingerTv,infoWordTv1,infoWordTv2,infoWordTv3,infoWordTv4,infoWordTv5,infoWordTv6,infoWordTv7,playerName,groupSize,musicSize;
    SeekBar bar;


    public MusicPlayer getMusicPlayer(){
        return musicPlayer;
    }
    private void 权限(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readView();
        setActions();
        musicPlayer=MusicPlayer.getMusicPlayer(MainActivity.this);
        refresh();
        权限();
        playingIsNull();
        new Thread(new Time(this)).start();
    }
    private void refresh(){                                                     //可变列表的刷新和事件
        refreshmain();
        refreshgroup();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        pathTemp.setText(UriUtils.getPath(MainActivity.this,uri));
    }
    private void refreshmain(){
        singleL.removeAllViews();
        int size=musicPlayer.getData().getMainList().size();
        musicSize.setText(size+"");
        for(int i=0;i<size;i++){
            final Music music=musicPlayer.getData().getMainList().get(i);
            LinearLayout songView=(LinearLayout)(getLayoutInflater().inflate(R.layout.item_music,null));

            View playBt=songView.findViewById(R.id.songItemPlay);
            TextView name=songView.findViewById(R.id.songItemName);
            TextView singer=songView.findViewById(R.id.songItemSinger);

            playBt.setTag(new Tag(music.id));

            name.setText(music.name);
            singer.setText(music.singer);
            playBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id=((Tag)view.getTag()).music;
                    selectGroup(-1);
                    selectMusic(id);
                }
            });
            singleL.addView(songView);
            songView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            songView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int id=((Tag)(view.findViewById(R.id.songItemPlay).getTag())).music;
                    final Music m=musicPlayer.getData().getMainList().get(id);

                    AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("歌曲信息：");
                    LayoutInflater li=getLayoutInflater();
                    final View v=li.inflate(R.layout.add_music,null);
                    final TextView name=v.findViewById(R.id.addMusicName);
                    final TextView singer=v.findViewById(R.id.addMusicSinger);
                    final TextView path=v.findViewById(R.id.addMusicPath);
                    final TextView word=v.findViewById(R.id.addMusicWord);
                    final Button pathB=v.findViewById(R.id.addMusicPathB);
                    final Button wordB=v.findViewById(R.id.addMusicWordB);
                    name.setText(m.name);
                    singer.setText(m.singer);
                    path.setText(m.path);
                    word.setText(m.wordPath);
                    dialog.setView(v);
                    pathB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, 1);
                            pathTemp=(EditText) path;
                        }
                    });
                    wordB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, 1);
                            pathTemp=(EditText) word;
                        }
                    });
                    dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String n=name.getText()+"";
                            String s=singer.getText()+"";
                            String p=path.getText()+"";
                            String w=word.getText()+"";
                            musicPlayer.saveMusic(id,n,s,p,w);
                            refresh();
                        }
                    });
                    dialog.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeMusic(id);
                            refresh();
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
        }
    }
    private void refreshgroup(){
        groupL.removeAllViews();
        int size=musicPlayer.getData().getGroupList().size();
        groupSize.setText(size+"");
        for(int i=0;i<size;i++){
            final Group group=musicPlayer.getData().getGroupList().get(i);
            LinearLayout groupView=(LinearLayout)(getLayoutInflater().inflate(R.layout.item_group,null));
            Button add=groupView.findViewById(R.id.groupItemAdd);

            TextView name=groupView.findViewById(R.id.groupItemName);
            TextView sizeV=groupView.findViewById(R.id.groupItemSize);

            add.setTag(new Tag(-1,group.id));

            name.setText(group.name);
            sizeV.setText(group.getList().size()+"");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int id=((Tag)view.getTag()).group;
                    final Group g=musicPlayer.getData().getGroupList().get(id);

                    final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);

                    dialog.setTitle("要添加的歌曲：");
                    LayoutInflater li=getLayoutInflater();
                    final View v=li.inflate(R.layout.add_to_group,null);
                    final LinearLayout listv=v.findViewById(R.id.linkList);

                    dialog.setView(v);
                    final AlertDialog dlg=dialog.show();

                    int mSize=musicPlayer.getData().getMainList().size();
                    for(int j=0;j<mSize;j++){
                        final Music mTemp=musicPlayer.getData().getMainList().get(j);
                        LinearLayout linkItemView=(LinearLayout)(getLayoutInflater().inflate(R.layout.item_link,null));
                        TextView linkN=linkItemView.findViewById(R.id.linkSongName);
                        TextView linkS=linkItemView.findViewById(R.id.linkSongSinger);
                        linkN.setText(mTemp.name);
                        linkS.setText(mTemp.singer);

                        linkItemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                musicPlayer.addToGroup(mTemp.id,g.id);
                                refreshgroup();
                                dlg.dismiss();
                            }
                        });
                        listv.addView(linkItemView);
                    }
                }
            });
            groupL.addView(groupView);
            groupView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id=((Tag)(view.findViewById(R.id.groupItemAdd).getTag())).group;
                    if(musicPlayer.getData().getGroupList().get(id).show){
                        musicPlayer.getData().getGroupList().get(id).show=false;
                    }else musicPlayer.getData().getGroupList().get(id).show=true;
                    refreshgroup();
                }
            });
            groupView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int id=((Tag)(view.findViewById(R.id.groupItemAdd).getTag())).group;
                    final Group g=musicPlayer.getData().getGroupList().get(id);

                    AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("歌单：");
                    LayoutInflater li=getLayoutInflater();
                    final View v=li.inflate(R.layout.add_group,null);
                    final TextView name=v.findViewById(R.id.addGroupName);
                    name.setText(g.name);
                    dialog.setView(v);
                    dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String n=name.getText()+"";
                            musicPlayer.saveGroup(id,n);
                            refreshgroup();
                        }
                    });
                    dialog.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeGroup(id);
                            refreshgroup();
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
            if(group.show){
                int gsize=group.getList().size();
                for(int j=0;j<gsize;j++){
                    Music music=group.getList().get(j);
                    LinearLayout songView=(LinearLayout)(getLayoutInflater().inflate(R.layout.item_music,null));
                    View splayBt=songView.findViewById(R.id.songItemPlay);
                    TextView sname=songView.findViewById(R.id.songItemName);
                    TextView ssinger=songView.findViewById(R.id.songItemSinger);

                    splayBt.setTag(new Tag(music.id,i));

                    sname.setText(music.name);
                    ssinger.setText(music.singer);
                    splayBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id=((Tag)(view.getTag())).music;
                            selectGroup(group.id);
                            selectMusic(id);
                        }
                    });
                    groupL.addView(songView);
                    songView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    songView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            Tag tag=(Tag)view.findViewById(R.id.songItemPlay).getTag();
                            final int id=tag.music;
                            final int gid=tag.group;

                            AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("是否从歌单中删除此歌曲？");
                            dialog.setPositiveButton("取消", null);
                            dialog.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    removeMusic(id,gid);
                                    refreshgroup();
                                }
                            });
                            dialog.show();
                            return false;
                        }
                    });
                }
            }
        }
    }
    private void readView(){
        listBt=findViewById(R.id.list);
        infoBt=findViewById(R.id.info);
        singleBt=findViewById(R.id.singleB);
        groupBt=findViewById(R.id.groupB);
        listL=findViewById(R.id.listV);
        infoL=findViewById(R.id.infoV);
        singleL=findViewById(R.id.singleL);
        groupL=findViewById(R.id.groupL);
        singleSv=findViewById(R.id.singleSV);
        groupSv=findViewById(R.id.groupSV);
        infoNameTv=findViewById(R.id.songName);
        infoSingerTv=findViewById(R.id.songSinger);
        infoWordTv1=findViewById(R.id.word1);
        infoWordTv2=findViewById(R.id.word2);
        infoWordTv3=findViewById(R.id.word3);
        infoWordTv4=findViewById(R.id.word4);
        infoWordTv5=findViewById(R.id.word5);
        infoWordTv6=findViewById(R.id.word6);
        infoWordTv7=findViewById(R.id.word7);
        playerName=findViewById(R.id.playerName);
        bar=findViewById(R.id.playerBar);
        playerInfo=findViewById(R.id.playerInfo);
        playerS=findViewById(R.id.playerS);
        playerX=findViewById(R.id.playerX);
        playerB=findViewById(R.id.playerB);
        playerG=findViewById(R.id.playerG);
        addGroup=findViewById(R.id.addGroup);
        groupSize=findViewById(R.id.groupSize);
        addMusic=findViewById(R.id.addMusic);
        musicSize=findViewById(R.id.musicSize);
    }
    private void setActions(){                                                          //固定UI的事件
        listBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listL.setVisibility(View.VISIBLE);
                infoL.setVisibility(View.GONE);
            }
        });
        infoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listL.setVisibility(View.GONE);
                infoL.setVisibility(View.VISIBLE);
            }
        });
        singleBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleSv.setVisibility(View.VISIBLE);
                groupSv.setVisibility(View.GONE);
            }
        });
        groupBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleSv.setVisibility(View.GONE);
                groupSv.setVisibility(View.VISIBLE);
            }
        });
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("导入歌曲：");
                LayoutInflater li=getLayoutInflater();
                final View v=li.inflate(R.layout.add_music,null);
                final TextView name=v.findViewById(R.id.addMusicName);
                final TextView singer=v.findViewById(R.id.addMusicSinger);
                final TextView path=v.findViewById(R.id.addMusicPath);
                final TextView word=v.findViewById(R.id.addMusicWord);
                final Button pathB=v.findViewById(R.id.addMusicPathB);
                final Button wordB=v.findViewById(R.id.addMusicWordB);
                dialog.setView(v);
                pathB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 1);
                        pathTemp=(EditText) path;
                    }
                });
                wordB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 1);
                        pathTemp=(EditText) word;
                    }
                });
                dialog.setPositiveButton("导入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n=name.getText()+"";
                        String s=singer.getText()+"";
                        String p=path.getText()+"";
                        String w=word.getText()+"";
                        musicPlayer.addMusic(n,s,p,w);
                        refreshmain();
                    }
                });
                dialog.show();
            }
        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("新建歌单：");
                LayoutInflater li=getLayoutInflater();
                final View v=li.inflate(R.layout.add_group,null);
                final TextView name=v.findViewById(R.id.addGroupName);
                dialog.setView(v);
                dialog.setPositiveButton("新建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n=name.getText()+"";
                        musicPlayer.addGroup(n);
                        refreshgroup();
                    }
                });
                dialog.show();
            }
        });
        playerG.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("当前歌单");
                if(musicPlayer.getSelectedId()==-1)
                    dialog.setMessage("(所有歌曲)");
                else
                    dialog.setMessage((musicPlayer.getSelectedId()+1)+"："+musicPlayer.getData().getGroupList().get(musicPlayer.getSelectedId()).name);
                dialog.show();
                return false;
            }
        });
        playerG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("选择歌单");
                LayoutInflater li=getLayoutInflater();
                final View v=li.inflate(R.layout.add_to_group,null);
                final LinearLayout listv=v.findViewById(R.id.linkList);

                dialog.setView(v);
                final AlertDialog dlg=dialog.show();
                int gSize=musicPlayer.getData().getGroupList().size();
                LinearLayout linkItemViewN=(LinearLayout)(getLayoutInflater().inflate(R.layout.item_link,null));
                TextView linkNN=linkItemViewN.findViewById(R.id.linkSongName);
                linkNN.setText("（所有歌曲）");
                linkItemViewN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectGroup(-1);
                        refreshgroup();
                        dlg.dismiss();
                    }
                });
                listv.addView(linkItemViewN);
                for(int j=0;j<gSize;j++){
                    final Group gTemp=musicPlayer.getData().getGroupList().get(j);
                    LinearLayout linkItemView=(LinearLayout)(getLayoutInflater().inflate(R.layout.item_link,null));
                    TextView linkN=linkItemView.findViewById(R.id.linkSongName);
                    linkN.setText(gTemp.name);

                    linkItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectGroup(gTemp.id);
                            refreshgroup();
                            dlg.dismiss();
                        }
                    });
                    listv.addView(linkItemView);
                }
            }
        });
        playerS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.getMusicId()==-1)return;
                Music mtemp=musicPlayer.getSelected().get(musicPlayer.getMusicId());
                int gId=musicPlayer.getSelected().indexOf(mtemp);
                if(gId>0)
                    selectMusic(musicPlayer.getSelected().get(gId-1).id);
                else pause();
            }
        });
        playerX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.getMusicId()==-1)return;
                Music mtemp=musicPlayer.getSelected().get(musicPlayer.getMusicId());
                int gId=musicPlayer.getSelected().indexOf(mtemp);
                if(gId<musicPlayer.getSelected().size()-1)
                    selectMusic(musicPlayer.getSelected().get(gId+1).id);
                else pause();
            }
        });
        playerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.playing()) {
                    pause();
                }
                else {
                    play();
                }
            }
        });
        playerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMode();
            }
        });
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b==true)
                    moveToTime(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isBarChanging=true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isBarChanging=false;
            }
        });


    }


//******↓播放器操作**********↑UI界面**********
    private void moveToTime(int time){
        musicPlayer.changeBar(time);
    }
    private void play(){
        if(musicPlayer.getMusicId()==-1)return;
        playerB.setText("暂停");
        musicPlayer.play();
    }
    private void pause(){
        playerB.setText("播放");
        musicPlayer.pause();
    }
    private void selectMusic(int id){
        if(id<0||id>=musicPlayer.getData().getMainList().size())return;
        pause();
        String nTemp=musicPlayer.getData().getMainList().get(id).name;
        playerName.setText(nTemp);
        infoNameTv.setText(nTemp);
        infoSingerTv.setText(musicPlayer.getData().getMainList().get(id).singer);
        musicPlayer.play(id);
        play();
    }
    private void selectGroup(int id){
        musicPlayer.selectGroup(id);
        List<Music> list=musicPlayer.getSelected();
        if(list.size()==0)playingIsNull();
        else{
            selectMusic(list.get(0).id);
        }
    }
    private boolean isBarChanging=false;
    public void refreshPlayer(){
        bar.setMax(musicPlayer.player.getDuration());
        if(!isBarChanging)bar.setProgress(musicPlayer.player.getCurrentPosition());
        if(musicPlayer.player.getDuration()==musicPlayer.player.getCurrentPosition())
            playFinish();
    }
    private void playingIsNull(){
        playerB.setText("播放");
        playerName.setText("当前无播放歌曲");
        infoNameTv.setText("当前无播放歌曲");
        infoSingerTv.setText("");
        infoWordTv1.setText("");
        infoWordTv2.setText("");
        infoWordTv3.setText("");
        infoWordTv4.setText("");
        infoWordTv5.setText("");
        infoWordTv6.setText("");
        infoWordTv7.setText("");
        musicPlayer.play(-1);
        musicPlayer.player.reset();
    }
    private void removeMusic(int id,int group){
        musicPlayer.removeFromGroup(id,group);
        int group_ing=musicPlayer.getSelectedId();
        int music_ing=musicPlayer.getMusicId();
        if(group_ing==group&&music_ing==id){
            if(musicPlayer.getSelected().size()==0)playingIsNull();
            else selectMusic(musicPlayer.getSelected().get(0).id);
        }
    }
    private void removeMusic(int id){
        musicPlayer.removeMusic(id);
        if(musicPlayer.getMusicId()==id){
            if(musicPlayer.getSelected().size()==0)playingIsNull();
            else selectMusic(musicPlayer.getSelected().get(0).id);
        }

    }
    private void removeGroup(int id){
        musicPlayer.removeGroup(id);
        if(musicPlayer.getSelectedId()==id)selectGroup(-1);
        if(musicPlayer.getSelectedId()>id)musicPlayer.setSelectedId(musicPlayer.getSelectedId()-1);
    }
    public void setWord(String[] word){
        if(word.length!=7)return;
        infoWordTv1.setText(word[0]);
        infoWordTv2.setText(word[1]);
        infoWordTv3.setText(word[2]);
        infoWordTv4.setText(word[3]);
        infoWordTv5.setText(word[4]);
        infoWordTv6.setText(word[5]);
        infoWordTv7.setText(word[6]);
    }
    private void playFinish(){
        if(musicPlayer.getMusicId()==-1)return;
        Music music=musicPlayer.getSelected().get(musicPlayer.getMusicId());
        List<Music> list=musicPlayer.getSelected();
        if(mode==1){
            if(list.size()==0)playingIsNull();
            int n=list.indexOf(music);
            if(n<list.size()-1){
                selectMusic(list.get(n+1).id);
            }else playingIsNull();
        }else if(mode==2){
            musicPlayer.changeBar(0);
            play();
        }else if(mode==3){
            if(list.size()==0)playingIsNull();
            int n=list.indexOf(music);
            if(n<list.size()-1){
                selectMusic(list.get(n+1).id);
            }else selectMusic(list.get(0).id);
        }else if(mode==4){
            if(list.size()==0)playingIsNull();
            int n=list.indexOf(music);
            int n2;
            for(;;){
                n2=new Random().nextInt(list.size()-1);
                if(n2!=n)break;
            }
            selectMusic(list.get(n2).id);
        }
    }
    private int mode=1;//1:顺序 2:单曲 3:列表 4:随机
    private void changeMode(){
        if(mode==1){
            mode=2;
            playerInfo.setText("单曲");
        }else if(mode==2){
            mode=3;
            playerInfo.setText("列表");
        }else if(mode==3){
            mode=4;
            playerInfo.setText("随机");
        }else if(mode==4){
            mode=1;
            playerInfo.setText("顺序");
        }
    }

}
