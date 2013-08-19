package com.eatlink.eatly;

import java.util.Random;

import android.util.Log;

public class EatlyPickUp {
    private final String TAG = getClass().getSimpleName();
    // private HashMap<String, String> restaurantMap = new HashMap<String,
    // String>();
    private Random r;
    private int m_maxsize = 0;
    private EatlyDataBase m_db = EatlyDataBase.getInstance();;

    public EatlyPickUp() {
        // restaurantMap.put("0", "McDonald's");
        // restaurantMap.put("1", "MOS");
        // restaurantMap.put("2", "金園排骨");
        // restaurantMap.put("3", "四川成都小炒");
        // restaurantMap.put("4", "悟饕");
        // restaurantMap.put("5", "正群牛肉麵");
        // restaurantMap.put("6", "自助餐");
        // restaurantMap.put("7", "牛騷味");
        // restaurantMap.put("8", "鬍鬚張");
        // restaurantMap.put("9", "眾饕");
        r = new Random();

    }

    public String select() {
        String selectedshop = null;
        m_maxsize = m_db.getDBSize();
        if (m_maxsize > 0) {
            int key = r.nextInt(m_maxsize - 0) + 0;
            selectedshop = m_db.pickOnefromDB(Integer.toString(key));
            printDebug("selection: " + selectedshop);
        } else {
            selectedshop = "no data!!";
        }
        return selectedshop;
    }

    private void printDebug(String s) {
        Log.d(TAG, s);
    }
}
