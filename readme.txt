项目接入海外版本的google firebase和广告的Applovin踩的坑记录一波

1: cocos引擎打包的包名和icon修改后不会生效，需要手动全局搜索后替换一波

2: gradle 版本需要升级，修改build.gradle的classpath 'com.android.tools.build:gradle:4.0.1'修改gradle-wrapper.properties的distributionUrl=https\://services.gradle.org/distributions/gradle-6.5.1-all.zip

3: 默认cocos生成的项目会有三个module,但是实际上用不了那么多，一个app就够了，需要修改setting.gradle里面的include ':libcocos2dx',':game，instantapp' 去掉game，instantapp，避免构造的时候出错，另外还可以节省打包时间

4: proj.android-studio/jni/CocosAndroid.mk里面的LOCAL_MODULE := cocos2djs_shared修改为LOCAL_MODULE := cocos2djs

5: proj.android-studio/app/build.gradle里面的资源拷贝方式需要更新为

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
6: 可以将keystore设置到gradle.propreties配置里面，在build.gradle里面配置标识signingConfig的release和debug版本在buildTypes调用
   if (project.hasProperty("RELEASE_STORE_FILE")) {signingConfig signingConfigs.release}

7: PROP_APP_ABI需要调整配置的支持库armeabi-v7a:arm64-v8a  （影响打包的大小和支持的机型范围）

8: 国内的镜像库什么的一点都不好用，有的全有的不全，有的版本还有问题，建议直接翻墙，翻墙一时爽一直翻一直爽

9: 版本号起作用的是versionCode 每次加1就好了，但是显示的是versionName "1.7.5"  建议用对照表对应起来，同步去改，另外这里去写更新文档真的很重要，你根本想象不到会有多奇葩的需求和版本骚操作

10：variant.AssetsProvider更新为variant.mergeAssetsProvider.get()

11：minifyEnabled设置为true的时候需要shrinkResources同步设置为true 另外设置为true的时候不能通过映射的方式获取资源，不然打包的时候映射的资源不会被打进去，对包体要求不大的情况下可以先对资源缩减下手。

12：优化构建速度
	在gradle.properties中添加：
        org.gradle.jvmargs=-Xmx4096m  

        #设置jvm虚拟机内存
	
org.gradle.parallel=true
	
org.gradel.daemon=true
	
org.gradel.configureondemand=tru
	在building.gradle里面加入
	android {
		dexOptions {
    		dexInProcess true
    		preDexLibraries true
    		javaMaxHeapSize "6g"
		}
	}

13：判断是否支持google gms 

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

14: 获取facebook的hash key (用openSSL 然后通过keystore也可以获取，但是我测试下来两个不一样，不过facebook后台可以填多个hash key ,都写进去肯定没错，不然错的话会报
	Login Error: There is an error in logging you into this application. Please try again later”)
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



