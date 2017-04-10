package com.alexjlockwood.activity.transitions;

import android.os.Build;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     2017/4/6
 * Description:
 * Fix History:
 * =============================
 */

public class Utils {

    /**
     * 判断是不是萝莉炮(5.0)版本
     */
    public static boolean isLOLLIPOP(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return true;
        return false;
    }
}
