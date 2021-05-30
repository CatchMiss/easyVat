package com.moptim.easyvat.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.moptim.easyvat.R;

public class SoundManager {
	private static SoundManager instance;
	public static SoundManager getInstance() {
		if(instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}

	private Map<Integer, Integer> soundPoolMap;// 声音集合
    private SoundPool soundPool;// 声音池
    
	private SoundManager() {
		soundPoolMap = new HashMap<>();
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	}
	
	public void init(Context context) {
		soundPoolMap.put(R.raw.result_show, soundPool.load(context, R.raw.result_show, 1));
        soundPoolMap.put(R.raw.ou, soundPool.load(context, R.raw.ou, 1));
        soundPoolMap.put(R.raw.os, soundPool.load(context, R.raw.os, 1));
        soundPoolMap.put(R.raw.od, soundPool.load(context, R.raw.od, 1));
        soundPoolMap.put(R.raw.up, soundPool.load(context, R.raw.up, 1));
        soundPoolMap.put(R.raw.down, soundPool.load(context, R.raw.down, 1));
        soundPoolMap.put(R.raw.left, soundPool.load(context, R.raw.left, 1));
        soundPoolMap.put(R.raw.right, soundPool.load(context, R.raw.right, 1));
        soundPoolMap.put(R.raw.kbq, soundPool.load(context, R.raw.kbq, 1));
	}
	
	public void playSound(int res) {
        soundPool.play(soundPoolMap.get(res), 1, 1, 0, 0, 1);
    }
}
