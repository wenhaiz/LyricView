package com.wenhaiz.lyricview.utils;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.wenhaiz.lyricview.bean.Lyric;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词工具类
 * <p>
 * Created by Wenhai on 2017/9/17.
 */

public class LyricUtil {
    private static final String TAG = "LyricUtil";

    private static final String TAG_TITLE = "ti";
    private static final String TAG_ARTIST = "ar";
    private static final String TAG_ALBUM = "al";
    private static final String TAG_CREATOR = "by";
    private static final String TAG_AUTHOR = "au";
    private static final String TAG_OFFSET = "offset";

    public static Lyric parseLyric(InputStream inputStream, String Encoding) {
        Lyric lyric = new Lyric();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Encoding));
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line, lyric);
            }
            Collections.sort(lyric.getSentenceList());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return lyric;
    }

    public static Lyric parseLyric(File file, String encoding) {
        try {
            InputStream fileInputStream = new FileInputStream(file);
            return parseLyric(fileInputStream, encoding);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    public static Lyric parseLyric(String lrcStr, String encoding) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(lrcStr.getBytes(encoding));
            return parseLyric(inputStream, encoding);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    private static void parseLine(String line, Lyric lyric) {
        if (TextUtils.isEmpty(line)) {
            return;
        }

        line = line.trim();
        Matcher lineMatcher = Pattern.compile("((\\[\\d\\d:\\d+\\.\\d+])+)(.*)").matcher(line);
        if (!lineMatcher.matches()) {
            parseLyricInfo(line, lyric);
            return;
        }
        String times = lineMatcher.group(1);
        String text = lineMatcher.group(3);

        Matcher timeMatcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d+)]").matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            String mill = timeMatcher.group(3);
            //毫秒可能是三位的，两位的需要乘以10
            int mulFactor = mill.length() == 2 ? 10 : 1;
            long mil = Long.parseLong(mill);
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * mulFactor;
            lyric.addSentence(text, time);
        }

    }

    private static void parseLyricInfo(String line, Lyric lyric) {
        Pattern pattern = Pattern.compile("\\[([a-z]+):(.*)]");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String tag = matcher.group(1);
            String content = matcher.group(2);
            addLyricInfo(tag, content, lyric);
        }
    }

    private static void addLyricInfo(String tag, String content, Lyric lyric) {
        switch (tag) {
            case TAG_ALBUM:
                lyric.setAlbum(content);
                break;
            case TAG_ARTIST:
                lyric.setArtist(content);
                break;
            case TAG_OFFSET:
                lyric.setOffset(Integer.valueOf(content));
                break;
            case TAG_TITLE:
                lyric.setTitle(content);
                break;
            case TAG_CREATOR:
                lyric.setCreator(content);
                break;
            case TAG_AUTHOR:
                lyric.setAuthor(content);
                break;
        }
    }


    /**
     * Save lyric to local app directory
     *
     * @return Saved destination. Null if failed.
     */
    public static String saveLyric(Lyric lyric) {
        return "";
    }
}
