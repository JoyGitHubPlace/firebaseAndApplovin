��Ŀ���뺣��汾��google firebase�͹���Applovin�ȵĿӼ�¼һ��

1: cocos�������İ�����icon�޸ĺ󲻻���Ч����Ҫ�ֶ�ȫ���������滻һ��

2: gradle �汾��Ҫ�������޸�build.gradle��classpath 'com.android.tools.build:gradle:4.0.1'�޸�gradle-wrapper.properties��distributionUrl=https\://services.gradle.org/distributions/gradle-6.5.1-all.zip

3: Ĭ��cocos���ɵ���Ŀ��������module,����ʵ�����ò�����ô�࣬һ��app�͹��ˣ���Ҫ�޸�setting.gradle�����include ':libcocos2dx',':game��instantapp' ȥ��game��instantapp�����⹹���ʱ��������⻹���Խ�ʡ���ʱ��

4: proj.android-studio/jni/CocosAndroid.mk�����LOCAL_MODULE := cocos2djs_shared�޸�ΪLOCAL_MODULE := cocos2djs

5: proj.android-studio/app/build.gradle�������Դ������ʽ��Ҫ����Ϊ

	copy {

????????
		from "${sourceDir}"

????????
		include "assets/**"

? ??????
		include "res/**"

? ??????
		include "src/**"

? ??????
		include "jsb-adapter/**"

? ??????
		into outputDir
	

}
6: ���Խ�keystore���õ�gradle.propreties�������棬��build.gradle�������ñ�ʶsigningConfig��release��debug�汾��buildTypes����
   if (project.hasProperty("RELEASE_STORE_FILE")) {signingConfig signingConfigs.release}

7: PROP_APP_ABI��Ҫ�������õ�֧�ֿ�armeabi-v7a:arm64-v8a  ��Ӱ�����Ĵ�С��֧�ֵĻ��ͷ�Χ��

8: ���ڵľ����ʲô��һ�㶼�����ã��е�ȫ�еĲ�ȫ���еİ汾�������⣬����ֱ�ӷ�ǽ����ǽһʱˬһֱ��һֱˬ

9: �汾�������õ���versionCode ÿ�μ�1�ͺ��ˣ�������ʾ����versionName "1.7.5"  �����ö��ձ��Ӧ������ͬ��ȥ�ģ���������ȥд�����ĵ���ĺ���Ҫ����������󲻵����ж����������Ͱ汾ɧ����

10��variant.AssetsProvider����Ϊvariant.mergeAssetsProvider.get()

11��minifyEnabled����Ϊtrue��ʱ����ҪshrinkResourcesͬ������Ϊtrue ��������Ϊtrue��ʱ����ͨ��ӳ��ķ�ʽ��ȡ��Դ����Ȼ�����ʱ��ӳ�����Դ���ᱻ���ȥ���԰���Ҫ�󲻴������¿����ȶ���Դ�������֡�

12���Ż������ٶ�
	��gradle.properties����ӣ�
        org.gradle.jvmargs=-Xmx4096m  

        #����jvm������ڴ�
	
org.gradle.parallel=true
	
org.gradel.daemon=true
	
org.gradel.configureondemand=tru
	��building.gradle�������
	android {
		dexOptions {
    		dexInProcess true
    		preDexLibraries true
    		javaMaxHeapSize "6g"
		}
	}

13���ж��Ƿ�֧��google gms 

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

14: ��ȡfacebook��hash key (��openSSL Ȼ��ͨ��keystoreҲ���Ի�ȡ�������Ҳ�������������һ��������facebook��̨��������hash key ,��д��ȥ�϶�û����Ȼ��Ļ��ᱨ
	Login Error: There is an error in logging you into this application. Please try again later��)
	try {
            PackageInfo info = getPackageManager().getPackageInfo( getPackageName(),  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String KeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("=========",KeyHash);
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }



