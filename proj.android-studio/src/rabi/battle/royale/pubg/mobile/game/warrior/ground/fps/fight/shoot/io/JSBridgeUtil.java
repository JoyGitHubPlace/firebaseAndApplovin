package rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io;


import android.util.Log;


import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * 游戏中JAVA 数据 传递到 游戏脚本 逻辑
 * @author Y-way
 *
 */
public class JSBridgeUtil {

    private static final String JSCallBackFuncName = "hy.nativeCallback(\'%s\')";

    public static final String JS_EVENT_INIT = "appinit";
    public static final String JS_EVENT_EXCEPTION="exception";

    public static final String JS_EVENT_ADBOX_BANER = "adbanner";
    public static final String JS_EVENT_ADBOX_INTERSTITIAL= "adinterstitial";

    //登陆
    public static final int NATIVE_CMD_LOGIN = 2;
    //退出
    public static final int NATIVE_CMD_LOGOUT = 3;
    //结束游戏
    public static final int NATIVE_CMD_EXIT_GAME = 4;
    //支付
    public static final int NATIVE_CMD_PAY= 5;
    //APPLOVIN 初始化SDK
    public static final int NATIVE_CMD_SDKINFO = 11;
    //视频广告 初始化
    public static final int NATIVE_CMD_AD_INIT = 12;
    //展示banner广告
    public static final int NATIVE_CMD_AD_BANNER = 13;
    //展示全屏交互广告
    public static final int NATIVE_CMD_AD_INTERSTITIAL = 14;
    //播放激励视频广告
    public static final int NATIVE_CMD_AD_VIDEO = 15;
    //播放已经加载的视频
    public static final int NATIVE_CMD_AD_PLAY_VIDEO = 16;

    public static final int NATIVE_CMD_APP_SHARE_TEXT = 17;
    public static final int NATIVE_CMD_APP_SHARE_IMAGE = 18;

    public static final String NATIVE_EVENT_AD_VIDEO_CALLBACK = "101";
    public static final String NATIVE_EVENT_AD_INIT = "102";
    public static final String NATIVE_EVENT_LOGIN = "103";

    public static final String NATIVE_EVENT_PAY_BACK = "104";
    public static final int NATIVE_CMD_RATE = 101;

    private static AppActivity appActivity;


    public static void init(AppActivity appAct){
        appActivity = appAct;

        //base info
        JSBridgeUtil.showBaseInfo();
        //sdk init

        //是否支持google service
       // String boolNUm = appAct.isSupportGoogle()+"";
       // Log.d("AppActivity", boolNUm);



    }
    public  static void showBaseInfo(){
        /*if(true){
            String phoneInfo = "Product: " + android.os.Build.PRODUCT;
            phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI;
            phoneInfo += ", TAGS: " + android.os.Build.TAGS;
            phoneInfo += ", VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE;
            phoneInfo += ", MODEL: " + android.os.Build.MODEL;
            phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
            phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
            phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
            phoneInfo += ", BRAND: " + android.os.Build.BRAND;
            phoneInfo += ", BOARD: " + android.os.Build.BOARD;
            phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
            phoneInfo += ", ID: " + android.os.Build.ID;
            phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER;
            phoneInfo += ", USER: " + android.os.Build.USER;
            phoneInfo += ", Language: " + Locale.getDefault().getLanguage();

            Log.e("AppUtil", phoneInfo);
        }*/
    }
    public static void testSendNotification(String messageBody){
        Log.i("JSBridgeUtil", "testSendNotification"+messageBody);
        appActivity.sendMessage(messageBody);
    }
    public static void JSInterface(final String json) {
        Log.i("JSBridgeUtil", json);
        if(appActivity == null){
            Log.d("JSBridgeUtil", "appActivity is null.");
            JSBridgeUtil.notifyEventToJS(JSBridgeUtil.JS_EVENT_INIT , -1);
            return;
        }
        appActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d("JSBridgeUtil", "runOnUiThread1 " + json);
                    JSONObject jo = new JSONObject(json);
                    int cmd = jo.optInt("cmd");
                    final String data = jo.optString("data");
                    Log.d("JSBridgeUtil", "runOnUiThread2 " + data);
                    switch (cmd)
                    {
                        case JSBridgeUtil.NATIVE_CMD_LOGIN:   //登陆
                            JSONObject l_Data = new JSONObject(data);
                            String plat = l_Data.optString("plat");
                            appActivity.signIn(plat);
                            break;
                        case JSBridgeUtil.NATIVE_CMD_LOGOUT:   //登出
                            appActivity.signOut();
                            break;
                        case JSBridgeUtil.NATIVE_CMD_EXIT_GAME:   //结束游戏
                            break;
                        case JSBridgeUtil.NATIVE_CMD_PAY:   //支付
                            JSONObject p_Data = new JSONObject(data);
                            String count = p_Data.optString("count");
                            Log.d("JSBridgeUtil", "count: " + count);
                            appActivity.openPay(count);
                            break;
                        case JSBridgeUtil.NATIVE_CMD_SDKINFO:   //sdk初始化
                            appActivity.SDKInit();
                            break;
                        case JSBridgeUtil.NATIVE_CMD_AD_INIT:   //视频new 和加载
                            appActivity.initMaxRewardedAd();
                            break;
                        case JSBridgeUtil.NATIVE_CMD_AD_BANNER:   //banner
                            break;
                        case JSBridgeUtil.NATIVE_CMD_AD_INTERSTITIAL:   //全屏交互广告
                            /*Intent intent = new Intent(appActivity, AppLovinInterstitialActivity.class);
                            JSONObject I_Data = new JSONObject(data);
                            String I_posId = I_Data.optString("posId");
                            Log.e("JSBridgeUtil", "appActivity.showInterstitialAd:" + I_posId);
                            intent.putExtra("posId", I_posId);
                             appActivity.startActivity(intent);*/
                            break;
                        case JSBridgeUtil.NATIVE_CMD_AD_VIDEO:   //播放视频
                            //Intent intent = new Intent(appActivity,AppLovinRewardActivity.class);
                           // appActivity.startActivity(intent);
                            JSONObject sData = new JSONObject(data);
                            String posId = sData.optString("posId");
                            Log.e("JSBridgeUtil", "appActivity.showAd:" + posId);
                            appActivity.showAd(posId);
                            break;
                        case JSBridgeUtil.NATIVE_CMD_AD_PLAY_VIDEO:   //播放已经加载的视频
                            break;
                        case JSBridgeUtil.NATIVE_CMD_RATE:   //用户进度上报
                            JSONObject rData = new JSONObject(data);
                            String rateId = rData.optString("posId");
                            Log.e("JSBridgeUtil", "appActivity.fireBaseRateReport:" + rateId);
                            appActivity.fireBaseRateReport(rateId);
                            break;
                        case JSBridgeUtil.NATIVE_CMD_APP_SHARE_TEXT:
                            appActivity.shareApp("text");
                            break;
                        case JSBridgeUtil.NATIVE_CMD_APP_SHARE_IMAGE:
                            appActivity.shareApp("image");
                            break;
                        default:
                            Log.e("JSBridgeUtil", "Unknown Command:" + cmd);
                            break;
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSBridgeUtil", "Exception:" + e.getMessage());
                    JSBridgeUtil.notifyExceptionToJS(e);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * JAVA 异常信息, 通知到 游戏脚本逻辑
     * @param e
     */
    public static void notifyExceptionToJS(Exception e) {
        JSBridgeUtil.notifyEventToJS(JSBridgeUtil.JS_EVENT_EXCEPTION, 1, e.getMessage());
    }
    /**
     * JAVA 与脚本逻辑 通信数据.
     * @param type 			通信数据事件类型
     * @param result			数据返回值. 0:成功, 否则失败.
     * @param desc			结果描述
     * @param jsonData		通信数据内容, json格式字符窜.
     */
    public static void notifyEventToJS(final String type, final int result, final String desc, final String jsonData) {
        final String retJson = String.format("{\"type\":\"%s\", \"result\":%d, \"desc\":\"%s\", \"data\":\"%s\"}",
                type, result, URLEncoder.encode(desc), URLEncoder.encode(jsonData));
        Cocos2dxHelper.runOnGLThread(new Runnable(){
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(String.format(JSCallBackFuncName, retJson));
            }
        });
    }
    /**
     * JAVA 与脚本逻辑 通信数据.
     * @param type 			通信数据事件类型
     * @param result			数据返回值. 0:成功, 否则失败.
     * @param desc			结果描述
     */
    public static void notifyEventToJS(final String type, final int result, final String desc) {
        final String retJson = String.format("{\"type\":\"%s\", \"result\":%d, \"desc\":\"%s\", \"data\":\"{}\"}",
                type, result, desc);

        Cocos2dxHelper.runOnGLThread(new Runnable(){
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(String.format(JSCallBackFuncName, retJson));
            }
        });
    }

    /**
     * JAVA 与脚本逻辑 通信数据.
     * @param type 			通信数据事件类型
     * @param result			数据返回值. 0:成功, 否则失败.
     */
    public static void notifyEventToJS(final String type, final int result) {
        final String retJson = String.format("{\"type\":\"%s\", \"result\":%d, \"desc\":\"%s\", \"data\":{}}",
                type, result, "");

        Cocos2dxHelper.runOnGLThread(new Runnable(){
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(String.format(JSCallBackFuncName, retJson));
            }
        });
    }
    /**
     * JAVA 与脚本逻辑 通信数据.
     * @param type 			通信数据事件类型
     */
    public static void notifyEventToJS(final String type) {
        final String retJson = String.format("{\"type\":\"%s\", \"result\":%d, \"desc\":\"%s\", \"data\":{}}",
                type, 0, "");

        Cocos2dxHelper.runOnGLThread(new Runnable(){
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(String.format(JSCallBackFuncName, retJson));
            }
        });
    }
    /**
     * JAVA 与脚本逻辑 通信数据.
     * @param data 自定义格式
     */
    public static void notifyCustomDataToJS(final String data) {
        Cocos2dxHelper.runOnGLThread(new Runnable(){
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(String.format(JSCallBackFuncName, data));
            }
        });
    }
}
