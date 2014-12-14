package com.zerochip.baimiwaiter;

import com.zerochip.baimiwaiter.R.string;
import com.zerochip.util.GetNetWorkState;
import com.zerochip.util.SimpleTextToSpeech;
import com.zerochip.util.WorkContext;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.speech.tts.TextToSpeech;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * ClassInfo:百米登录Activity
 * 
 * @author Kevin
 * 
 */
public class LoginActivity extends Activity
{
    // 定义debug 信息
    private static final String TAG = "com.zerochip.baimiwaiter.LoginActivity";
    public static final boolean DEBUG = true;
    // 定义Context
    public WorkContext mWorkContext = null;
    // 验证码布局
    private LinearLayout verifyLinearLayout = null;
    // 定义 EditText View
    // 用户名
    private EditText usernameEditText = null;
    // 密码
    private EditText passwdEditText = null;
    // 验证码
    private EditText verifyEditText = null;
    // 定义 Button View
    // 验证码换一张按键
    private Button verifyButton = null;
    // 登录按键
    private Button loginButton = null;
    private String usernameString = null;
    private String passwdString = null;
    // TTS
    private static final int REQ_CHECK_TTS_DATA = 110; // TTS数据校验请求值
    private float mTtsSpeechRate = 1.0f; // 朗读速率
    public final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case View.FOCUS_LEFT:
            case View.FOCUS_RIGHT:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWorkContext = new WorkContext();
        mWorkContext.mContext = this;
        mWorkContext.mActivity = this;
        mWorkContext.mHandler = mHandler;
        mWorkContext.mResources = getResources();
        mWorkContext.mGetNetWorkState = new GetNetWorkState(
                mWorkContext.mContext);
        mWorkContext.mSimpleTextToSpeech = new SimpleTextToSpeech(
                mWorkContext.mContext, mTtsSpeechRate);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        Log.e(TAG, "onResume");
        
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        InitView();
        ViewSetListener();
        checkTtsData();
    }

    /**
     * @Function:Login 按键动作处理
     */
    private Runnable LoginButtonClickRunable = new Runnable()
    {
        public void run()
        {
            // 判断用户名和密码是否为空
            usernameString = usernameEditText.getText().toString();
            passwdString = passwdEditText.getText().toString();
            if (DEBUG)
                Log.i(TAG, "username = " + usernameString);
            if (DEBUG)
                Log.i(TAG, "passwd = " + passwdString);
            if (usernameString.isEmpty())
            {
                MakeToast(mWorkContext.mResources
                        .getString(R.string.please_input_username));
                usernameEditText.requestFocus();
                return;
            }
            else
                if (passwdString.isEmpty())
                {
                    MakeToast(mWorkContext.mResources
                            .getString(R.string.please_input_passwd));
                    passwdEditText.requestFocus();
                    return;
                }
            // 判断网络连接状态
            CheckNetWorkState();
            mWorkContext.mSimpleTextToSpeech.TtsSpeechString(usernameString + passwdString);
        }
    };
    /**
     * @Function:验证码换一张按键动作处理
     */
    private Runnable VerifyButtonClickRunable = new Runnable()
    {
        public void run()
        {
        }
    };

    /**
     * @Function:给View增加Listener
     */
    private void ViewSetListener()
    {
        verifyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                mWorkContext.mHandler.post(VerifyButtonClickRunable);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mWorkContext.mHandler.post(LoginButtonClickRunable);
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * @Function：初始化所有的view 类
     */
    private void InitView()
    {
        // 验证码布局
        verifyLinearLayout = (LinearLayout) findViewById(R.id.verify_code_linelayout);
        // 定义 EditText View
        // 用户名
        usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
        // 密码
        passwdEditText = (EditText) findViewById(R.id.login_passwd_edit_text);
        // 验证码
        verifyEditText = (EditText) findViewById(R.id.login_verify_edittext);
        // 定义 Button View
        // 验证码换一张按键
        verifyButton = (Button) findViewById(R.id.login_verify_button);
        // 登录按键
        loginButton = (Button) findViewById(R.id.login_button);
    }

    /**
     * @Function:Toast 显示
     * @param showString
     *            显示字符
     */
    private void MakeToast(String showString)
    {
        Toast.makeText(mWorkContext.mContext, showString, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * @Function：根据GetNetWorkState 工具类获取wifi,GPRS 状态和设置
     * @return : false: 网络状态未连接 true: 网络状态已连接
     */
    public boolean CheckNetWorkState()
    {
        boolean mNetState = false;
        mNetState = mWorkContext.mGetNetWorkState.getWifiState();
        if (!mNetState)
        {
            mNetState = mWorkContext.mGetNetWorkState.getGPRSState();
        }
        if (!mNetState)
        {
            // 显示Dialog让用户选择退出或者设置网络
            setNetWrok();
        }
        if (DEBUG)
            Log.i(TAG, "netstate = " + mNetState);
        return mNetState;
    }

    /**
     * @Function: 显示dialog提示用户是否设置网络
     */
    private void setNetWrok()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(mWorkContext.mResources
                .getString(R.string.network_dialog_title));
        builder.setMessage(mWorkContext.mResources
                .getString(R.string.network_dialog_message));
        builder.setPositiveButton(mWorkContext.mResources
                .getString(R.string.network_dialog_setting_button),
                new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = null;
                        /**
                         * 判断手机系统的版本！如果API大于10 就是3.0+
                         * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                         */
                        if (android.os.Build.VERSION.SDK_INT > 10)
                        {
                            intent = new Intent(
                                    android.provider.Settings.ACTION_WIFI_SETTINGS);
                        }
                        else
                        {
                            intent = new Intent();
                            ComponentName component = new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.WirelessSettings");
                            intent.setComponent(component);
                            intent.setAction("android.intent.action.VIEW");
                        }
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(mWorkContext.mResources
                .getString(R.string.network_dialog_cancel_button),
                new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * 校验TTS引擎安装及资源状态
     * 
     * @return
     */
    public boolean checkTtsData()
    {
        try
        {
            if(DEBUG) Log.e(TAG, "checkTtsData");
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, REQ_CHECK_TTS_DATA);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    /**
     * @Function 提示用户是否重装TTS引擎数据的对话框
     */
    private void notifyReinstallDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle(
                        mWorkContext.mResources
                                .getString(R.string.tts_dialog_title))
                .setMessage(
                        mWorkContext.mResources
                                .getString(R.string.tts_dialog_message))
                .setPositiveButton(
                        mWorkContext.mResources
                                .getString(R.string.tts_dialog_verigy_button),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                // 触发引擎在TTS引擎在设备上安装资源文件
                                Intent dataIntent = new Intent();
                                dataIntent
                                        .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                startActivity(dataIntent);
                            }
                        })
                .setNegativeButton(
                        mWorkContext.mResources
                                .getString(R.string.tts_dialog_cancel_button),
                        null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_CHECK_TTS_DATA)
        {
            switch (resultCode)
            {
            case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS: // TTS引擎可用
                // 针对于重新绑定引擎，需要先shutdown()
                if (null != mWorkContext.mSimpleTextToSpeech)
                {
                    mWorkContext.mSimpleTextToSpeech.TtsSpeechStop();// 停止当前发声
                    mWorkContext.mSimpleTextToSpeech.TtsSpeechShutDown(); // 释放资源
                }
                if(DEBUG) Log.e(TAG, "onActivityResult");
                mWorkContext.mSimpleTextToSpeech = new SimpleTextToSpeech(
                        mWorkContext.mContext, mTtsSpeechRate); // 创建TextToSpeech对象
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA: // 数据错误
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA: // 缺失数据资源
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME: // 缺少数据存储量
                notifyReinstallDialog(); // 提示用户是否重装TTS引擎数据的对话框
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL: // 检查失败
            default:
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
