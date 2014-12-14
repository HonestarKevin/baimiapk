package com.zerochip.util;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;

/**
 * @Function 确认网络连接状态
 * @author Kevin.Xu
 *
 */
public class GetNetWorkState
{
    private Context mContext = null;
    private ConnectivityManager networkConnectivityManager;

    /**
     * @Function: 确认网络链接状态
     * @param mWorkContext
     */
    public GetNetWorkState(Context mContext)
    {
        super();
        this.mContext = mContext;
        this.networkConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    /**
     * @Funciton: 确认wifi连接状态
     * @return：
     *      true:wifi 连接正常
     *      false:wifi 连接异常
     */
    public boolean getWifiState()
    {
        boolean wifiStateFlag = false;
        if (networkConnectivityManager.getActiveNetworkInfo() != null)
        {
            if (networkConnectivityManager.getActiveNetworkInfo().isAvailable())
            {
                State wifiState = networkConnectivityManager.getNetworkInfo(
                        ConnectivityManager.TYPE_WIFI).getState();
                // 判断为wifi状态是否是链接状态
                if (wifiState == State.CONNECTED
                        || wifiState == State.CONNECTING)
                {
                    wifiStateFlag = true;
                }
            }
        }
        return wifiStateFlag;
    }
    /**
     * @Funciton: 确认GPRS连接状态
     * @return：
     *      true:GPRS 连接正常
     *      false:GPRS 连接异常
     */
    public boolean getGPRSState()
    {
        boolean gprsStateFlag = false;
        if (networkConnectivityManager.getActiveNetworkInfo() != null)
        {
            if (networkConnectivityManager.getActiveNetworkInfo().isAvailable())
            {
                State gprsState = networkConnectivityManager.getNetworkInfo(
                        ConnectivityManager.TYPE_MOBILE).getState();
                // 判断为wifi状态是否是链接状态
                if (gprsState == State.CONNECTED
                        || gprsState == State.CONNECTING)
                {
                    gprsStateFlag = true;
                }
            }
        }
        return gprsStateFlag;
    }
}
