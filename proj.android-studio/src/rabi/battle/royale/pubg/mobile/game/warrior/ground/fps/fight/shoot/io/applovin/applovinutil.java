package rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io.applovin;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io.JSBridgeUtil;
import rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io.AppActivity;
/**
 * ADBox
 */
public class applovinutil  {
    private static String LOG_TAG = "ADBox";
    private static boolean mIsAdSDKInit = false;
    private static boolean mIsSDKIniting = false;
    private static boolean mIsLoadRewardVideo = false;
    private static Context mContext;

    private int retryAttempt;
    public static void init(Context context){
        applovinutil.mContext = context;
    }

    public static void initAdSDK(final String jsonData){
        Log.i(LOG_TAG, "========>>>>applovinUtil initAdSDK");
        try {
            if(mIsSDKIniting){
                return;
            }
            mIsSDKIniting = true;
            JSONObject jo = new JSONObject(jsonData);



        }
        catch (Exception e){
            JSBridgeUtil.notifyExceptionToJS(e);
        }
    }

    public static void showBanner(final String jsonData){
        Log.i(LOG_TAG, "========>>>>applovinUtil showBanner");
        try {
            if(!mIsAdSDKInit){
                JSBridgeUtil.notifyEventToJS(JSBridgeUtil.JS_EVENT_ADBOX_BANER, -1, "Adbox is not init");
                return;
            }
            JSONObject jo = new JSONObject(jsonData);

        }
        catch (Exception e){
            e.printStackTrace();
            JSBridgeUtil.notifyExceptionToJS(e);
        }
    }
    /**
     * 插屏广告
     * @param jsonData
     */
    public static void showInterstitialAd(final String jsonData){
        Log.i(LOG_TAG, "========>>>>applovinUtil showInterstitialAd");
        try {
            if(!mIsAdSDKInit){
                JSBridgeUtil.notifyEventToJS(JSBridgeUtil.JS_EVENT_ADBOX_INTERSTITIAL, -1, "Adbox is not init");
                return;
            }

        }
        catch (Exception e){
            e.printStackTrace();
            JSBridgeUtil.notifyExceptionToJS(e);
        }
    }

    /**
     * 加载激励视频
     * @param jsonData
     */
    public static void loadVideo(final String jsonData){
         Log.i(LOG_TAG, "========>>>> loadVideo");
        try {
            if (!mIsAdSDKInit) {
                //JSBridgeUtil.notifyEventToJS(JSBridgeUtil.JS_EVENT_ADBOX_VIDEO, ADBox_Const.VideoNeedInit, "Adbox is not init");
                return;
            }
            if(mIsLoadRewardVideo){
                //JSBridgeUtil.notifyEventToJS(JSBridgeUtil.JS_EVENT_ADBOX_VIDEO, ADBox_Const.VideoIsLoading, "rewardVideo is loading.");
                return;
            }
            mIsLoadRewardVideo = true;

        }
        catch (Exception e){
            e.printStackTrace();
            JSBridgeUtil.notifyExceptionToJS(e);
        }
    }
    /**
     * 展示视频
     * json参数:
     *  {
     *      "adid":"rewardedVideo:006",
     *      "extra":"String"
     *  }
     *  adid:激励视频ID
     *  extra:播放视频透传参数
     * @param jsonData
     */
    public void showVideo(final String jsonData){
        Log.i(LOG_TAG, "========>>>> showVideo");

    }

}
