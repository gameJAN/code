package com.biyesheji.code.util;

import java.util.Random;

/*字符串操作工具类*/
public class StringUtil {
    /*判断字符串是否为空*/

    public static boolean isEmpty(String str){
        if (str == null || str.trim().equals("")){
            return true;
        }
        return false;
    }
    /*判断字符串是否不为空*/

    public static boolean isNotEmpty(String str){
        if (str != null && !str.trim().equals("")){
            return true;
        }
        return false;
    }

    /*生成六位随机数*/
    public static String genSixRandom(){
        Random random = new Random();
        String result = "";
        for (int i =0 ; i < 6 ; i++ ){
            result += random.nextInt(10);
        }
        return result;
    }

}
