package com.mobo.funplay.gamebox.helper;

import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.Nullable;

import com.mobo.funplay.gamebox.utils.MoboLogger;

import java.util.HashMap;

public class DefImageHelper {
    private static final DefImageHelper helper = new DefImageHelper();

    private HashMap<Key, BitmapDrawable> maps;

    private DefImageHelper() {}

    private void init() {
        if (maps == null) {
            maps = new HashMap<>(16);
        }
    }

    public static void clear() {
        if (helper.maps != null) {
            helper.maps.clear();
        }
    }

    public static void put(BitmapDrawable map, int id, int width, int height) {
        helper.init();
        Key key = new Key(id, width, height);
        helper.maps.put(key, map);
        MoboLogger.debug("DefImageHelper", "save image");
    }

    public static BitmapDrawable get(int id, int width, int height) {
        helper.init();
        return helper.maps.get(new Key(id, width, height));
    }


    private static class Key {
        int resId, width, height;

        public Key(int resId, int width, int height) {
            this.resId = resId;
            this.width = width;
            this.height = height;
        }

        @Override
        public int hashCode() {
            return resId + 31 * (width * 31 + height);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Key) {
                Key other = (Key) obj;
                return resId == other.resId && width == other.width && height == other.height;
            }
            return false;
        }
    }
}
