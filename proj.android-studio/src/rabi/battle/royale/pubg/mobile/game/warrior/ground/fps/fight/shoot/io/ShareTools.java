package rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

public class ShareTools {




    public static boolean shareImageToApp(Activity context,Bitmap bitmap, String bitName) {
        String fileName;
        File file;
        String brand = Build.BRAND;

        if (brand.equals("xiaomi")) {
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else if (brand.equalsIgnoreCase("Huawei")) {
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else {
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + bitName;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            saveSignImage(context,bitName,bitmap);
            return true;
        } else {
            Log.v("saveBitmap brand", "" + brand);
            file =new File(fileName);
        }
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                if(Build.VERSION.SDK_INT >= 29){
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA,  file.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    shareImage(context,uri);
                }else{
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);

                }

            }
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "FileNotFoundException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e("IOException", "IOException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.e("IOException", "IOException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        return true;

    }
    public static void shareImage(Activity context,Uri uri){
        new Share2.Builder(context)
                .setContentType(ShareContentType.IMAGE)
                .setShareFileUri(uri)
                .setTitle("Share Image")
                .build()
                .shareBySystem();
    }
    public static void saveSignImage(Activity context,String fileName, Bitmap bitmap) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
                shareImage(context,uri);
            }
        } catch (Exception e) {
        }
    }


}