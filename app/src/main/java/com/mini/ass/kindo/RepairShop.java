package com.mini.ass.kindo;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RepairShop {
    public static String TAG = "RepairShop";

    private Map<String, RepairType> mRepairs;
    private Context mContext;

    public enum RepairType {
      DROP,
    };

    public RepairShop(Context context) {
        this.mRepairs = new HashMap<>();
        this.mContext = context;
    }

    public void add(String selector, RepairType type) {
        this.mRepairs.put(selector, type);
    }

    public String getRepairSauce() {
        String baseTemplate = this.mContext.getString(R.string.repair_template);
        String baseDropTemplate = this.mContext.getString(R.string.repair_drop_template);

        Set<Map.Entry<String, RepairType>> entrySet = this.mRepairs.entrySet();
        Iterator<Map.Entry<String, RepairType>> iterator = entrySet.iterator();

        String dropElements = "";
        while (iterator.hasNext()) {
            Map.Entry<String, RepairType> entry = iterator.next();

            if (entry.getValue() == RepairType.DROP) {
                dropElements += baseDropTemplate.replace("{selector}", entry.getKey());
            }
        }

        String cooked = baseTemplate.replace("{sauce}", dropElements);
        Log.d(RepairShop.TAG, cooked);
        return cooked;
    }

}
