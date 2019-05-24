package kr.co.gubed.habit2good.gpoint.util;

import android.support.v4.util.LruCache;

public class TextCache {
    private static TextCache instance;
    private LruCache<Object, Object> lru;

    private TextCache(){
        lru = new LruCache<>(5 * 1024 * 1024);
    }

    public static TextCache getInstance(){
        if( instance == null){
            instance = new TextCache();
        }
        return instance;
    }

    public LruCache<Object, Object> getLru(){
        return lru;
    }
}
