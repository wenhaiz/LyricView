package com.wenhaiz.lyricview.bean;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.annotation.NonNull;

public class Sentence implements Comparable<Sentence> {
    private String content;
    private long fromTime;
    private StaticLayout staticLayout;

    Sentence(String content, long fromTime) {
        this.content = content;
        this.fromTime = fromTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public void init(TextPaint paint, int width) {
        staticLayout = new StaticLayout(content, paint, width, Layout.Alignment.ALIGN_CENTER,
                1f, 0f, false);
    }

    public int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    public StaticLayout getStaticLayout() {
        return staticLayout;
    }

    @NonNull
    @Override
    public String toString() {
        return fromTime + ": " + content;
    }

    @Override
    public int compareTo(@NonNull Sentence sentence) {
        return (int) (this.fromTime - sentence.fromTime);
    }
}
