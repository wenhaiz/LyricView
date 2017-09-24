package com.wenhaiz.lyricdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wenhaiz.lyricview.LyricView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LyricView lyricView = (LyricView) findViewById(R.id.lyric);

    }
}
