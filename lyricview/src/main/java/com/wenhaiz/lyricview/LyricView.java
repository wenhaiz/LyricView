package com.wenhaiz.lyricview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;
import androidx.annotation.StringRes;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wenhaiz.lyricview.bean.Lyric;
import com.wenhaiz.lyricview.bean.Sentence;
import com.wenhaiz.lyricview.utils.ScreenUtil;

public class LyricView extends View {
    private Lyric mLyric;

    private TextPaint mPaint;
    private float mDividerHeight;
    private long mAnimationDuration;
    private int mNormalColor;
    private int mCurrentColor;
    private String mEmpty;
    private float mLrcPadding;
    private ValueAnimator mAnimator;
    private float mAnimateOffset;
    private long mNextTime = 0L;
    private int mCurrentLine = 0;

    public LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LyricView);
        float textSize = ta.getDimension(R.styleable.LyricView_lrcTextSize, 12);
        mDividerHeight = ta.getDimension(R.styleable.LyricView_lrcDividerHeight, ScreenUtil.dp2px(getContext(), 16));
        mAnimationDuration = ta.getInt(R.styleable.LyricView_lrcAnimationDuration, 1000);
        mAnimationDuration = (mAnimationDuration < 0) ? 1000 : mAnimationDuration;
        mNormalColor = ta.getColor(R.styleable.LyricView_lrcNormalColor, 0xFFFFFFFF);
        mCurrentColor = ta.getColor(R.styleable.LyricView_lrcHighLightColor, 0xFFFF4081);
        mEmpty = ta.getString(R.styleable.LyricView_lrcEmpty);
        mEmpty = TextUtils.isEmpty(mEmpty) ? getContext().getString(R.string.default_empty_text) : mEmpty;
        mLrcPadding = ta.getDimension(R.styleable.LyricView_lrcPadding, 0);
        ta.recycle();

        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(ScreenUtil.sp2px(getContext(),textSize));
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initLyric();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, mAnimateOffset);

        // 中心Y坐标
        float centerY = getHeight() / 2;

        mPaint.setColor(mCurrentColor);

        // 无歌词文件显示设置的默认文字
        if (!hasLyric()) {
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(mEmpty, mPaint, (int) getLrcWidth(),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            drawText(canvas, staticLayout, centerY - staticLayout.getHeight() / 2);
            return;
        }

        // 画当前行
        float currY = centerY - mLyric.getSentenceList().get(mCurrentLine).getHeight() / 2;
        drawText(canvas, mLyric.getSentenceList().get(mCurrentLine).getStaticLayout(), currY);

        // 画当前行上面的
        mPaint.setColor(mNormalColor);
        float upY = currY;
        for (int i = mCurrentLine - 1; i >= 0; i--) {
            upY -= mDividerHeight + mLyric.getSentenceList().get(i).getHeight();
            drawText(canvas, mLyric.getSentenceList().get(i).getStaticLayout(), upY);

            if (upY <= 0) {
                break;
            }
        }

        // 画当前行下面的
        float downY = currY + mLyric.getSentenceList().get(mCurrentLine).getHeight() + mDividerHeight;
        for (int i = mCurrentLine + 1; i < mLyric.getSentenceList().size(); i++) {
            drawText(canvas, mLyric.getSentenceList().get(i).getStaticLayout(), downY);
            if (downY + mLyric.getSentenceList().get(i).getHeight() >= getHeight()) {
                break;
            }
            downY += mLyric.getSentenceList().get(i).getHeight() + mDividerHeight;
        }
    }

    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(mLrcPadding, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private float getLrcWidth() {
        return getWidth() - mLrcPadding * 2;
    }

    /**
     * 设置歌词为空时屏幕中央显示的文字，如“暂无歌词”
     */
    public void setEmptyText(final String label) {
        mEmpty = label;
        invalidate();
    }

    public void setEmptyText(@StringRes final int resId) {
        mEmpty = getContext().getString(resId);
        setEmptyText(mEmpty);
    }

    /**
     * 刷新歌词进度
     *
     * @param time 当前播放时间
     */
    public void updateTime(final long time) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                // 避免重复绘制
                if (time < mNextTime) {
                    return;
                }
                for (int i = mCurrentLine; i < mLyric.getSentenceList().size(); i++) {
                    if (mLyric.getSentenceList().get(i).getFromTime() > time) {
                        mNextTime = mLyric.getSentenceList().get(i).getFromTime();
                        mCurrentLine = (i < 1) ? 0 : (i - 1);
                        animateNewline(i, true);
                        break;
                    } else if (i == mLyric.getSentenceList().size() - 1) {
                        // 最后一行
                        mCurrentLine = mLyric.getSentenceList().size() - 1;
                        mNextTime = Long.MAX_VALUE;
                        animateNewline(i, true);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 将歌词滚动到指定时间
     *
     * @param time 指定的时间
     */
    public void onDrag(final long time) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mLyric.getSentenceList().size(); i++) {
                    if (mLyric.getSentenceList().get(i).getFromTime() > time) {
                        if (i == 0) {
                            mCurrentLine = i;
                            initNextTime();
                        } else {
                            mCurrentLine = i - 1;
                            mNextTime = mLyric.getSentenceList().get(i).getFromTime();
                        }
                        animateNewline(i, false);
                        break;
                    }
                }
            }
        });
    }

    public boolean hasLyric() {
        return mLyric != null;
    }

    private void reset() {
        stopAnimation();
        mCurrentLine = 0;
        mNextTime = 0L;
        initLyric();
        invalidate();
    }

    private void initLyric() {
        if (getWidth() == 0 || mLyric == null
                || mLyric.getSentenceList() == null
                || mLyric.getSentenceList().isEmpty()) {
            return;
        }
        for (Sentence sentence : mLyric.getSentenceList()) {
            sentence.init(mPaint, (int) getLrcWidth());
        }
        initNextTime();
    }

    private void initNextTime() {
        if (mLyric.getSentenceList().size() > 1) {
            mNextTime = mLyric.getSentenceList().get(1).getFromTime();
        } else {
            mNextTime = Long.MAX_VALUE;
        }
    }

    public Lyric getLyric() {
        return mLyric;
    }

    public void setLyric(Lyric lyric) {
        this.mLyric = lyric;
        reset();
    }

    /**
     * 换行动画
     */
    private void animateNewline(int line, boolean animate) {
        stopAnimation();

        if (line <= 0 || !animate) {
            invalidate();
            return;
        }

        float prevHeight = mLyric.getSentenceList().get(line - 1).getHeight();
        float currHeight = mLyric.getSentenceList().get(line).getHeight();
        float totalOffset = (prevHeight + currHeight) / 2 + mDividerHeight;

        mAnimator = ValueAnimator.ofFloat(totalOffset, 0.0f);
        mAnimator.setDuration(mAnimationDuration * mLyric.getSentenceList().get(line).getStaticLayout().getLineCount());
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimateOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    private void stopAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

}
