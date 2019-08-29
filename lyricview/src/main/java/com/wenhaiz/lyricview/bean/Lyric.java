package com.wenhaiz.lyricview.bean;

import androidx.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

public class Lyric {
    private String title;
    private String artist;
    private String album;
    private String creator;
    private String author;
    private int offset;
    private long length;
    private List<Sentence> sentenceList = new ArrayList<>(100);

    public void addSentence(String content, long time) {
        sentenceList.add(new Sentence(content, time));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public List<Sentence> getSentenceList() {
        return sentenceList;
    }

    public void setSentenceList(List<Sentence> sentenceList) {
        this.sentenceList = sentenceList;
    }

    public class Sentence implements Comparable<Sentence> {
        private String content;
        private long fromTime;
        private StaticLayout staticLayout;

        public Sentence(String content, long fromTime) {
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
            staticLayout = new StaticLayout(content, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
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

        public String toString() {
            return String.valueOf(fromTime) + ": " + content;
        }

        @Override
        public int compareTo(@NonNull Sentence sentence) {
            return (int) (this.fromTime - sentence.fromTime);
        }
    }


}
