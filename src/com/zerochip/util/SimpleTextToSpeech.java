package com.zerochip.util;

import java.util.HashMap;

import com.zerochip.baimiwaiter.LoginActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @Function: 文字转换为语音工具类
 * @author Kevin.Xu
 * 
 */
public class SimpleTextToSpeech implements TextToSpeech.OnInitListener,
        TextToSpeech.OnUtteranceCompletedListener
{
    private static final String TAG = "com.zerochip.baimiwaiter.SimpleTextToSpeech";
    private static final boolean DEBUG = LoginActivity.DEBUG;
    
    private TextToSpeech mTextToSpeech = null; // TTS对象
    private Context mContext = null;
    private float mTtsSpeechRate = 1.0f; // 朗读速率

    public SimpleTextToSpeech(Context mContext, float mTtsSpeechRate)
    {
        super();
        this.mContext = mContext;
        this.mTtsSpeechRate = mTtsSpeechRate;
        if(DEBUG) Log.e(TAG, "SimpleTextToSpeech");
        mTextToSpeech = new TextToSpeech(mContext, this);
    }
    /** 
     * TTS引擎完成发声完成时回调方法
     * 
     * 1）stop()取消时也会回调
     * 2）需在onInit()内设置接口
     * 3）utteranceId由speak()时的请求参数设定
     * 参数key：TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
     */
    @Override
    public void onUtteranceCompleted(String utteranceId)
    {
        // TODO Auto-generated method stub
    }
    /**
     * TTS引擎初始化时回调方法
     * 
     * 引擎相关参数（音量、语速）等都需在这设置。
     * 1）创建完成后再去设置，会有意外的效果^^
     * 2）音量也可由AudioManager进行控制（和音乐一个媒体流类型）
     */
    @Override
    public void onInit(int status)
    {
        
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS)
        {
            if(DEBUG) Log.e(TAG, "onInit");
            mTextToSpeech.setSpeechRate(mTtsSpeechRate); // 设置朗读速率
            // 设置发声合成监听，注意也需要在onInit()中做才有效
            mTextToSpeech.setOnUtteranceCompletedListener(this);
        }
    }
    /**
     * @Function 将文字转换为语言播放
     * @param mString 播放内容
     * @return TextToSpeech 状态
     */
    public int TtsSpeechString(String mString)
    {
        int speechState = TextToSpeech.ERROR;
        if(DEBUG) 
                Log.i(TAG, "speech context:"+mString);
        if (mTextToSpeech != null)
        {
            /**
             * 叙述text。
             * 
             * 1） 参数2（int queueMode） 1.1）QUEUE_ADD：增加模式。增加在队列尾，继续原来的说话。
             * 1.2）QUEUE_FLUSH：刷新模式。中断正在进行的说话，说新的内容。 2）参数3（HashMap<String,
             * String> params） 2.1）请求的参数，可以为null。 2.2）注意KEY_PARAM_UTTERANCE_ID。
             */
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mString);
            speechState = mTextToSpeech.speak(mString, TextToSpeech.QUEUE_ADD,
                    params);
        }
        return speechState;
    }
    
    /**
     * @Function 停止当前发声，同时放弃所有在等待队列的发声
     * @return
     */
    public int TtsSpeechStop() {
        return (null == mTextToSpeech) ? TextToSpeech.ERROR : mTextToSpeech.stop();
    }
    /**
     * @Function释放资源（解除语音服务绑定）
     */
    public void TtsSpeechShutDown() {
        if (null != mTextToSpeech) {
            mTextToSpeech.shutdown();
        }
    }
    
    /**
     * @Function 将文本合成声音资源文件 
     * @param text
     * @param filename
     * @return
     */
    public int TtsSpeechSaveFile(String text, final String filename) {
        if(DEBUG) 
            Log.i(TAG, "speech context:"+text+" save name = "+filename);
        return (null == mTextToSpeech) ? TextToSpeech.ERROR : mTextToSpeech.synthesizeToFile(
                text, null, filename);
    }
}
