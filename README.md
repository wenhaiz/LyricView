# LyricView v 1.0.1

LyricView 是一个用于展示歌词的 Android 自定义 View，目前只支持 lrc 格式的文件。  

## 简介
在我开发的音乐播放器 [Listen All](https://github.com/wenhaiz/ListenAll) 中有展示歌词的功能，我比较了 [wangchenyan](https://github.com/wangchenyan) 和 [markzhai](https://github.com/markzhai) 的 LyricView ，发现他们都不太开放和友好，所以借助这两个库的成果，我写了这个开源库。  

在整体结构上，我参考了 [markzhai 的 LyricView](https://github.com/markzhai/LyricView)，在歌词展示上，我参考了[wangchenyan 的 LyricView](https://github.com/wangchenyan/LrcView)。  
目前看来，这个库是由两个库拼起来的，但是我想后续给它增加一些新功能，比如支持更多格式和更多操作等，在此也感谢两位的付出。 

## 使用
1. 导入依赖
```gradle
compile 'com.wenhaiz:lyricview:1.0.1'
```
2. 布局文件中添加 LyricView
```xml
<com.wenhaiz.lyricview.LyricView
    android:id="@+id/lyric"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
3. 使用 LyricUtil 加载歌词
```kotlin
//从网络加载歌词的代码由子线程执行
val thread = Thread {
  kotlin.run {
    try {
      val url = URL(lyricUrl)
      val inputStream = url.openConnection().getInputStream()
      val lyric = LyricUtil.parseLyric(inputStream, "utf-8")
      onLyricLoaded(lyric)
    } catch (e: Exception) {
      //...
    }
  }
}
thread.start()
```
> 目前支持通过流、文件和字符串加载歌词。
4. 为 LyricView 设置对应的 Lyric
```kotlin
lyricView.lyric = lyric
```
5. 更新歌词进度
```kotlin
//val timeInMills = ....
lyricView.updateTime(timeInMills)
```
> `updateTime` 方法接收一个 `long` 类型的参数，用于指定当前播放时间。
## 自定义属性
目前 LyricView 支持的属性如下：    

|xml 属性名|值类型|解释|
|:---------:|:------:|:----:|
|lrcTextSize|dimension|歌词文字大小|
|lrcDividerHeight|dimension|歌词行距|
|lrcNormalColor|reference/color|普通歌词文字颜色|
|lrcHighLightColor|reference/color|高亮歌词文字颜色|
|lrcAnimationDuration|integer|歌词换行动画时长|
|lrcEmpty|string|没有歌词时显示的内容（默认为「暂无歌词」）|
|lrcPadding|dimension|歌词内边距| 

## 更新日志
### V 1.0.1 2017-09-24
- 简化代码
### V 1.0.0 2017-09-19
- 支持从流、文件和字符串解析歌词
- 支持歌词属性（艺人、标题、延时等）
