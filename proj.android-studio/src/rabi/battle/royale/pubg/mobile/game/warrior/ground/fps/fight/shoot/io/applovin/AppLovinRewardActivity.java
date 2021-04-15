package rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io.applovin;
import rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io.AppActivity;
import rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io.JSBridgeUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

import java.util.concurrent.TimeUnit;

/**
 * ADBox
 */
public class AppLovinRewardActivity extends Activity implements MaxRewardedAdListener
{
    public MaxRewardedAd rewardedAd;
    private int           retryAttempt;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            rewardedAd = MaxRewardedAd.getInstance( "3987367d25fc6a37", this );
            rewardedAd.setListener(this);
            rewardedAd.loadAd();

    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {
        Log.e("AppLovinRewardActivity", "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {
        Log.e("AppLovinRewardActivity", "onRewardedVideoCompleted");
    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        Log.e("AppLovinRewardActivity", "onUserRewarded");
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        Log.e("AppLovinRewardActivity", "onAdLoaded");
        retryAttempt = 0;
        this.showAd();
    }

    @Override
    public void onAdLoadFailed(String adUnitId, int errorCode) {
        Log.e("AppLovinRewardActivity", "onAdLoadFailed");
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                rewardedAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        Log.e("AppLovinRewardActivity", "onAdDisplayed");
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        Log.e("AppLovinRewardActivity", "onAdHidden");
        rewardedAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        Log.e("AppLovinRewardActivity", "onAdClicked");
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
        Log.e("AppLovinRewardActivity", "onAdDisplayFailed");
        rewardedAd.loadAd();
    }
    public void showAd() {
        if ( rewardedAd.isReady() )
        {
            Log.e("AppLovinRewardActivity", "rewardedAd.showAd");
            rewardedAd.showAd();
        }else{
            Log.e("AppLovinRewardActivity", "rewardedAd.isReady is not");
        }
    }
}
