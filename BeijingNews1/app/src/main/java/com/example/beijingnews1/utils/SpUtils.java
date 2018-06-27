package com.example.beijingnews1.utils;
/*
 *  包名: com.example.beijingnews1.utils
 * Created by ASUS on 2017/12/18.
 *  描述: TODO
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SpUtils {
    public static boolean getBoolean(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key,false);

    }

    public static void putBoolean(Context context,String key,boolean value){




        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();

    }

    /**
     * 保持数据
     * @param context
     * @param key
     * @param values
     */
    public static  void putString(Context context,String key,String values){

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                String fileName = MD5Encoder.encode(key);
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files", fileName);

                File parentFile = file.getParentFile();//mnt/sdcard/beijingnews/files
                if (!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }


                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fileOutputStream=new FileOutputStream(file);
                fileOutputStream.write(values.getBytes());
                fileOutputStream.close();

            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {

            SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(key,values).commit();
        }


    }

    /**
     * 得到缓存的数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context,String key){

        String result = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(key);//llkskljskljklsjklsllsl

                ///mnt/sdcard/beijingnews/files/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files", fileName);


                if (file.exists()) {

                    FileInputStream is = new FileInputStream(file);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }

                    is.close();
                    stream.close();

                    result = stream.toString();


                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片获取失败");
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
            result = sp.getString(key, "");
        }
        return result;
    }

}
