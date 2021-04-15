/****************************************************************************
Copyright (c) 2015-2016 Chukong Technologies Inc.
Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDex;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kochava.base.Tracker;
import com.kochava.base.Tracker.Configuration;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import com.facebook.ads.AdSettings;

import org.json.JSONException;
import org.json.JSONObject;

import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;


public class AppActivity extends Cocos2dxActivity implements MaxRewardedAdListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private  String currentPosid = "";
    private static AppActivity mApp;

    //google
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    //facebook
    private static final String TAG = "FacebookLogin";
    private CallbackManager mCallbackManager;
    private boolean isFBLoggedIn;

    private String Plat = "GOOGLE";
    private BillingClient billingClient;
    private boolean isServiceConnected = false;

    private final int consumeImmediately = 0;

    private final int consumeDelay = 1;
    private String PayItemId ="diamond_60";
    private Toast   toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = this;
        // 初始化MultiDex
        MultiDex.install(this);
        if (!isTaskRoot()) {
            return;
        }


        mAuth = FirebaseAuth.getInstance();
        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //初始化
        //facebook
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code， 登陆成功，自己编写成功后的方法
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        toast=Toast.makeText(getApplicationContext(), "You canceled the login", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, "exception: " + exception);
                        toast=Toast.makeText(getApplicationContext(), "Login exception, please switch to another platform and try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        //检测登陆状态，可选功能。 在官方文档中也有介绍，判断本地是否有登陆缓存，直接登陆
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isFBLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.e(TAG, "isFBLoggedIn: " + isFBLoggedIn);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // DO OTHER INITIALIZATION BELOW
        SDKWrapper.getInstance().init(this);
        Tracker.configure(new Configuration(getApplicationContext()).setAppGuid("korabi-battle-royal-j0ujms").setLogLevel(Tracker.LOG_LEVEL_NONE));



        AdSettings.setDataProcessingOptions( new String[] {} );


        AppLovinSdk.getInstance( this ).setMediationProvider( AppLovinMediationProvider.MAX );
        AppLovinSdk.getInstance( this ).initializeSdk( new AppLovinSdk.SdkInitializationListener()
        {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration config)
            {
                mApp.initMaxRewardedAd();
            }
        } );
        JSBridgeUtil.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FirebaseMessaging", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.e("FirebaseMessaging token", msg);
                    }
                });

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        startPayConnect();

        try {
            PackageInfo info = getPackageManager().getPackageInfo( getPackageName(),  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String KeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //KeyHash 就是你要的，不用改任何代码  复制粘贴 ;
                Log.e("=========",KeyHash);
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }

    }
    //--------------------------------------pay---------------------------------------------------------------------------------------
    private boolean checkPlayServices()
    {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        999).show();
            }

            return false;
        }

        return true;

    }

    public void startPayConnect(){
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            queryAndConsumePurchase();

                            isServiceConnected = true;
                            reCount = 0;
                        } else {

                        }
                    }
                    @Override
                    public void onBillingServiceDisconnected() {

                        isServiceConnected = false;
                        Log.e(TAG, "startConnection:fail");
                    }
                });


    }
    private void queryAndConsumePurchase() {
        //queryPurchases() 方法会使用 Google Play 商店应用的缓存，而不会发起网络请求
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(BillingResult billingResult,
                                                          List<PurchaseHistoryRecord> purchaseHistoryRecordList) {
                        {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseHistoryRecordList != null) {
                                for (PurchaseHistoryRecord purchaseHistoryRecord : purchaseHistoryRecordList) {
                                    // Process the result.
                                    //确认购买交易，不然三天后会退款给用户
                                    try {
                                        Purchase purchase = new Purchase(purchaseHistoryRecord.getOriginalJson(), purchaseHistoryRecord.getSignature());
                                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                            //消耗品 开始消耗
                                            consumePuchase(purchase, consumeImmediately);
                                            //确认购买交易
                                            if (!purchase.isAcknowledged()) {
                                                acknowledgePurchase(purchase);
                                            }

                                            //TODO：这里可以添加订单找回功能，防止变态用户付完钱就杀死App的这种
                                            String orderId = purchase.getOrderId();
                                            JSONObject jo = new JSONObject();
                                            try {
                                                jo.put("PayItemId", PayItemId);
                                                jo.put("succ", 1);
                                                jo.put("orderId", orderId);
                                                jo.put("event", "find order");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_PAY_BACK, 0,  "payCallBackInfo", jo.toString());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
    }
    //消耗商品
    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "Acknowledge purchase success");
                } else {
                    Log.i(TAG, "Acknowledge purchase failed,code=" + billingResult.getResponseCode() + ",\nerrorMsg=" + billingResult.getDebugMessage());
                }
            }
        };
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
    }

    private void consumePuchase(final Purchase purchase, final int state) {
        ConsumeParams.Builder consumeParams = ConsumeParams.newBuilder();
        consumeParams.setPurchaseToken(purchase.getPurchaseToken());
        billingClient.consumeAsync(consumeParams.build(), new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                Log.i(TAG, "onConsumeResponse, code=" + billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "onConsumeResponse,code=BillingResponseCode.OK");
                    if (state == consumeImmediately) {

                    }
                } else {
                    //如果消耗不成功，那就再消耗一次
                    Log.i(TAG, "onConsumeResponse=getDebugMessage==" + billingResult.getDebugMessage());
                    if (state == consumeDelay && billingResult.getDebugMessage().contains("Server error, please try again")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queryAndConsumePurchase();
                            }
                        }, 5 * 1000);
                    }
                }
            }
        });
    }
    private String chooseItem(String count){
        String itemId ="diamond_"+count+"0";
        return itemId;
    }
    private  int reCount = 0;
    public  void openPay(String count){
        if(reCount>2){
            toast=Toast.makeText(getApplicationContext(), "your Google mobile service is Unavailable", Toast.LENGTH_SHORT);
            toast.show();
            startPayConnect();
            return;
        }
        if(!isServiceConnected){
            startPayConnect();
            toast=Toast.makeText(getApplicationContext(), "Waiting for connection", Toast.LENGTH_SHORT);
            toast.show();
            reCount++;
            return;
        }
        reCount = 0;
        PayItemId = chooseItem(count);

        Log.e(TAG, "openpay:itemId"+PayItemId+"/"+count);

        new Thread(new Runnable(){
            public void run(){
                Log.e(TAG, "enter new Thread");
                List<String> skuList = new ArrayList<>();
                skuList.add(PayItemId);
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                Log.i(TAG, "querySkuDetailsAsync=getResponseCode==" + billingResult.getResponseCode() + ",skuDetailsList.size=" + skuDetailsList.size());
                                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                                    Log.e(TAG, "onSkuDetailsResponse:success");
                                    if (skuDetailsList.size() > 0) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            Log.i(TAG, "Sku=" + sku + ",price=" + price);
                                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(skuDetails)
                                                    .build();
                                            int responseCode = billingClient.launchBillingFlow(mApp, billingFlowParams).getResponseCode();
                                            if (responseCode == BillingClient.BillingResponseCode.OK) {
                                                Log.i(TAG, "成功启动google支付");
                                            } else {
                                                //BILLING_RESPONSE_RESULT_OK	0	成功
                                                //BILLING_RESPONSE_RESULT_USER_CANCELED	1	用户按上一步或取消对话框
                                                //BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE	2	网络连接断开
                                                //BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE	3	所请求的类型不支持 Google Play 结算服务 AIDL 版本
                                                //BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE	4	请求的商品已不再出售
                                                //BILLING_RESPONSE_RESULT_DEVELOPER_ERROR	5	提供给 API 的参数无效。此错误也可能说明应用未针对 Google Play 结算服务正确签名或设置，或者在其清单中缺少必要的权限。
                                                //BILLING_RESPONSE_RESULT_ERROR	6	API 操作期间出现严重错误
                                                //BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED	7	未能购买，因为已经拥有此商品
                                                //BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED	8	未能消费，因为尚未拥有此商品
                                                Log.i(TAG, "LaunchBillingFlow Fail,code=" + responseCode);
                                            }
                                        }
                                    }
                                }else {
                                    Log.i(TAG, "Get SkuDetails Failed,Msg=" + billingResult.getDebugMessage());
                                }


                            }
                });
        }
        }).start();


    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK&& purchases != null) {
                for (final Purchase purchase : purchases) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        Log.i(TAG, "Purchase success");
                        //确认购买交易，不然三天后会退款给用户
                        if (!purchase.isAcknowledged()) {
                            acknowledgePurchase(purchase);
                        }
                        //消耗品 开始消耗
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                consumePuchase(purchase, consumeDelay);
                            }
                        }, 2000);


                        //TODO:发放商品
                        String orderId = purchase.getOrderId();
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("PayItemId", PayItemId);
                            jo.put("succ", 1);
                            jo.put("orderId", orderId);
                            jo.put("event", "normalPayEnd");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_PAY_BACK, 0,  "payCallBackInfo", jo.toString());


                    } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                        //需要用户确认
                        Log.i(TAG, "Purchase pending,need to check");

                    }
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                //付款失败
                Log.i(TAG, "Purchase cancel");
                JSONObject jo = new JSONObject();
                try {
                    jo.put("PayItemId", PayItemId);
                    jo.put("succ", 0);
                    jo.put("event", "Pay failed");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_PAY_BACK, 0,  "payCallBackInfo", jo.toString());

            } else {
                Log.i(TAG, "Pay result error,code=" + billingResult.getResponseCode() + "\nerrorMsg=" + billingResult.getDebugMessage());
                JSONObject jo = new JSONObject();
                try {
                    jo.put("PayItemId", PayItemId);
                    jo.put("succ", 2);
                    jo.put("event", billingResult.getDebugMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_PAY_BACK, 0,  "payCallBackInfo", jo.toString());

            }
        }
    };
    //-------------------------------------------------------------------------------------------------------------------------

    public static AppActivity getApp() {
        return mApp;
    }
    public void fireBaseRewardsReport(final String posId,final String state){
        Bundle params = new Bundle();
        params.putString("posId", posId);
        params.putString("state", state);
        mFirebaseAnalytics.logEvent("reward", params);
    }
    public void fireBaseRateReport(final String posId){
        Bundle params = new Bundle();
        params.putString("posId", posId);
        mFirebaseAnalytics.logEvent("Rate", params);
    }
    public  void SDKInit(){


    }

    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
        SDKWrapper.getInstance().setGLSurfaceView(glSurfaceView, this);

        return glSurfaceView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKWrapper.getInstance().onResume();
        Log.e(TAG, "queryPurchases");

        billingClient.queryPurchases(BillingClient.SkuType.INAPP);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKWrapper.getInstance().onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isTaskRoot()) {
            return;
        }

        SDKWrapper.getInstance().onDestroy();

    }

    private void handleFacebookAccessToken(AccessToken token) {
       // Log.e(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user,true);
                        } else {
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null,true);
                        }

                    }
                });
    }
    private void firebaseAuthWithGoogle(String idToken) {

        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("GoogleActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user,true);
                        } else {
                            updateUI(null,true);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    public void signIn(String plat) {
        Plat = plat;
        Log.e(TAG, "plat:"+plat);
        if(plat.equals("GOOGLE")){
            Log.e(TAG, "plat——google:"+plat);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else{
            Log.e(TAG, "plat-facebook:"+plat);
            LoginManager.getInstance().logInWithReadPermissions(mApp, Arrays.asList("public_profile"));
        }

    }
    // [END signin]

    public void signOut() {
        mAuth.signOut();

        if(Plat.equals("GOOGLE")){
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null,false);
                        }
                    });
        }else{
            LoginManager.getInstance().logOut();
            updateUI(null,false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKWrapper.getInstance().onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "Plat:" + Plat);
        if(Plat.equals("GOOGLE")){
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.e("GoogleActivity", "firebaseAuth:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    toast=Toast.makeText(getApplicationContext(), "Login exception, please switch to another platform and try again", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }else{

            }
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKWrapper.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKWrapper.getInstance().onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKWrapper.getInstance().onStop();
    }

    @Override
    public void onBackPressed() {
        SDKWrapper.getInstance().onBackPressed();
        super.onBackPressed();
    }

    public static int sum(int a, int b){
        return a + b;
    }
    public static void calljsfun(final String aa,final String message){
        String title = message;
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SDKWrapper.getInstance().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SDKWrapper.getInstance().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        SDKWrapper.getInstance().onStart();
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser,false);


    }
    private void updateUI(FirebaseUser user,boolean isLogin) {
        if (user != null) {
            if(!isServiceConnected){
                startPayConnect();
            }
            Log.e("GoogleActivity", "getName："+user.getDisplayName());
            Log.e("GoogleActivity", "getPhotoUrl："+user.getPhotoUrl());
            Log.e("GoogleActivity", "getUid："+user.getUid());
            Log.e("GoogleActivity", "getEmail："+user.getEmail());
            JSONObject jo = new JSONObject();
            try {
                jo.put("succ", 1);
                jo.put("uid", user.getUid());
                jo.put("email", user.getEmail());
                jo.put("nickName", user.getDisplayName());
                jo.put("avatar", user.getPhotoUrl());
                jo.put("loginType", Plat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_LOGIN, 0,  "loginInfo", jo.toString());
        } else {
            if(isLogin){
                JSONObject jo = new JSONObject();
                try {
                    jo.put("succ", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_LOGIN, 0,  "loginInfo", jo.toString());
            }
        }
        if (!NotificationsUtils.isNotificationEnabled(this)) {

            Intent localIntent = new Intent();
            ///< 直接跳转到应用通知设置的代码
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
            }
            else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", this.getPackageName());
                localIntent.putExtra("app_uid", this.getApplicationInfo().uid);
            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + this.getPackageName()));
            } else {
                ///< 4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", this.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", this.getPackageName());
                }
            }
            this.startActivity(localIntent);


        }
    }

    //------------------------------------------------rewardad---------------------------------------------------

    public MaxRewardedAd rewardedAd;
    private int   retryAttempt = 0;

    public  void initMaxRewardedAd(){
        Log.e("AppActivity", "initMaxRewardedAd");
        rewardedAd = MaxRewardedAd.getInstance( "3987367d25fc6a37", this );
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
        //订阅Rbrpush消息
        FirebaseMessaging.getInstance().subscribeToTopic("Rbrpush")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.e(TAG, msg);
                       // Toast.makeText(AppActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void sendMessage(String messageBody){

        if (!NotificationsUtils.isNotificationEnabled(this)) {
            Log.i("AppActivity", "sendMessage:isNotificationEnabled false");

            Intent localIntent = new Intent();
            ///< 直接跳转到应用通知设置的代码
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
            }
            else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", this.getPackageName());
                localIntent.putExtra("app_uid", this.getApplicationInfo().uid);
            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + this.getPackageName()));
            } else {
                ///< 4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", this.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", this.getPackageName());
                }
            }
            this.startActivity(localIntent);




        }else{
            Log.i("AppActivity", "sendMessage"+messageBody);
            try{

            }catch (Exception e){
                Log.i("AppActivity", "Exception:"+e);
            }
            Notification notification = null;
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, AppActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String id = "channelId";
                String name = "google";
                NotificationChannel channel = new NotificationChannel(id,name,NotificationManager.IMPORTANCE_LOW);
                //设置锁屏展示
                manager.createNotificationChannel(channel);
                notification = new Notification.Builder(this)
                        .setChannelId(id)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
                        .build();
            }else{
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
                        .build();
            }
            manager.notify(1,notification);
        }

    }
    @Override
    public void onRewardedVideoStarted(MaxAd ad) {
        Log.e("AppActivity", "onRewardedVideoStarted");
        this.fireBaseRewardsReport(currentPosid,"RewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {
        Log.e("AppActivity", "onRewardedVideoCompleted");
        this.fireBaseRewardsReport(currentPosid,"RewardedVideoCompleted");

    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        Log.e("AppActivity", "onUserRewarded");
        this.fireBaseRewardsReport(currentPosid,"UserRewarded");
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        //JSBridgeUtil.fireBaseReport("111","LoadedSuccess");
        Log.e("AppActivity", "LoadedSuccess");
        retryAttempt = 0;
        this.fireBaseRewardsReport(currentPosid,"AdLoaded");
        JSONObject jo = new JSONObject();
        try {
            jo.put("rewardStatus", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_AD_VIDEO_CALLBACK, 0,  "rewardAdState", jo.toString());
    }

    @Override
    public void onAdLoadFailed(String adUnitId, int errorCode) {
        this.fireBaseRewardsReport(currentPosid,"AdLoadFailed");
        Log.e("AppActivity", "onAdLoadFailed:"+adUnitId+" errorCode:"+String.valueOf(errorCode));
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed( new Runnable()
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
        Log.e("AppActivity", "onAdDisplayed");
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        Log.e("AppActivity", "onAdHidden");
        rewardedAd.loadAd();
        this.fireBaseRewardsReport(currentPosid,"AdHidden");
        JSONObject jo = new JSONObject();
        try {
            jo.put("rewardStatus", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_AD_VIDEO_CALLBACK, 0,  "rewardAdState", jo.toString());

    }

    @Override
    public void onAdClicked(MaxAd ad) {
        Log.e("AppActivity", "onAdClicked");
        this.fireBaseRewardsReport(currentPosid,"AdClicked");
        JSONObject jo = new JSONObject();
        try {
            jo.put("rewardStatus", 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_AD_VIDEO_CALLBACK, 0,  "rewardAdState", jo.toString());

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
        Log.e("AppActivity", "onAdDisplayFailed");
        rewardedAd.loadAd();
        this.fireBaseRewardsReport(currentPosid,"AdDisplayFailed");
        JSONObject jo = new JSONObject();
        try {
            jo.put("rewardStatus", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_AD_VIDEO_CALLBACK, 0,  "rewardAdState", jo.toString());
    }
    public void showAd(String posId) {
        currentPosid = posId;
        if ( rewardedAd.isReady() )
        {
            this.fireBaseRewardsReport(currentPosid,"showRewardAd");
            Log.e("AppActivity", "rewardedAd.showAd");
            rewardedAd.showAd();
            JSONObject jo = new JSONObject();
            try {
                jo.put("rewardStatus", 2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_AD_VIDEO_CALLBACK, 0,  "rewardAdState", jo.toString());
        }else{
            Log.e("AppActivity", "rewardedAd.isReady is not");
            JSONObject jo = new JSONObject();
            try {
                jo.put("rewardStatus", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSBridgeUtil.notifyEventToJS(JSBridgeUtil.NATIVE_EVENT_AD_VIDEO_CALLBACK, 0,  "rewardAdState", jo.toString());
           /* rewardedAd =null;
            rewardedAd = MaxRewardedAd.getInstance( "3987367d25fc6a37", this );
            rewardedAd.setListener(this);
            rewardedAd.loadAd();*/

        }
    }

    public void shareApp(String shareType)  {
        if(shareType.equals("text")){
            new Share2.Builder(this)
                    .setContentType(ShareContentType.TEXT)
                    .setTextContent("In this game, can you be the final winner！##https://play.google.com/store/apps/details?id=rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io")
                    .setTitle("Rabi Battle Royale")
                    .build()
                    .shareBySystem();
        }else if(shareType.equals("image")){
            Resources res=getResources();
            Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.share);
            if(ShareTools.shareImageToApp(this,bmp,"share")){
                Log.e("AppActivity", "shareApp");

            }else{
                Log.e("AppActivity", "shareApp");
            }
        }


    }





}
