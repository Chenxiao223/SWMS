package com.zjfd.chenxiao.DHL.util;

import android.content.Context;
import android.media.MediaPlayer;


public class MediaUtil {
    MediaPlayer mediaPlayer;
    Context context;
    public MediaUtil(Context context){
        this.context=context;
    }
    public void music(int res){
        mediaPlayer= MediaPlayer.create(context, res);//播放raw里的歌曲
        mediaPlayer.start();//播放
    }
}
