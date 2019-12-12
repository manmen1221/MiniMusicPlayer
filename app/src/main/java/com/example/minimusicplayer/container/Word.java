package com.example.minimusicplayer.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Word {
    static int YAN_SHI=2000;

    private List<WordLine> value=new ArrayList<>();
    public Word(String path){
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            String line = null;
            while(null!=(line = reader.readLine())){
                int[] n={-1,-1,-1,-1};
                for(int i=0;i<line.length();i++){
                    if(line.charAt(i)=='['){
                        n[0]=i;
                        continue;
                    }
                    if(line.charAt(i)==':'){
                        n[1]=i;
                        continue;
                    }
                    if(line.charAt(i)=='.'){
                        n[2]=i;
                        continue;
                    }
                    if(line.charAt(i)==']'){
                        n[3]=i;
                        break;
                    }
                }

                WordLine wordLine=new WordLine();
                try{
                    String min=line.substring(n[0]+1,n[1]);
                    String sec=line.substring(n[1]+1,n[3]);
                    String w=line.substring(n[3]+1);
                    wordLine.time=(int)(Integer.parseInt(min)*60+Double.parseDouble(sec))*1000+YAN_SHI;
                    wordLine.word=w;
                    value.add(wordLine);
                }catch (Exception e){
                    try {
                        wordLine.time = -1;
                        wordLine.word = line.substring(n[1]+1,n[3]);
                        value.add(wordLine);
                    }catch (Exception e2){
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            value.clear();
        }


        for(int i=0;i<value.size();i++){
            System.out.println(i+"："+value.get(i).time+"，"+value.get(i).word);
        }
    }
    public String[] getWord(int time){
        int location=find(time);
        if(location==-1)location=value.size()-1;
        String[] string=new String[7];
        for(int i=0;i<7;i++){
            int j=location-3+i;
            if(j<0||j>=value.size())
                string[i]="";
            else string[i]=value.get(j).word;
        }
        System.out.println(string);
        return string;
    }
    private int find(int time){
        for(int i=0;i<value.size();i++){
            if(value.get(i).time>time){
                return i;
            }
        }
        return -1;
    }
    class WordLine{
        int time;
        String word;
    }
}

