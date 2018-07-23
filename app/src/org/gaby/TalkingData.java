package org.gaby;

import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TDGAItem;
import com.tendcloud.tenddata.TDGAVirtualCurrency;
import com.tendcloud.tenddata.TalkingDataGA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TalkingData {

    private static void log(String s) {
        JsBridge.log("[TalkingData]" + s);
    }

    /**
     * 统计账户
     * @param id
     * @param name
     * @param sex
     */
    public static void logined(String id, String name, String sex) {
        log("logined id=" + id + " name=" + name);
        try {
            TDGAAccount account = TDGAAccount.setAccount(id);
            account.setAccountName(name);
            if (sex != null && sex.equals("1")) {
                account.setGender(TDGAAccount.Gender.MALE);
            } else if (sex != null && sex.equals("2")) {
                account.setGender(TDGAAccount.Gender.FEMALE);
            }
            account.setAccountType(TDGAAccount.AccountType.REGISTERED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 充值请求
     */
    public static void onChargeRequest(String json) {
        try {
            JSONObject obj=new JSONObject(json);
           String orderId=obj.optString("orderId");
           String name= obj.optString("name");
           String money= obj.optString("money");
            TDGAVirtualCurrency.onChargeRequest(orderId, name, Integer.parseInt(money), "CNY", Integer.parseInt(money), "Android");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 充值成功
     * @param orderId
     */
    public static void onChargeSuccess(String orderId) {
        try {
            TDGAVirtualCurrency.onChargeSuccess(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 赠予虚拟币
     * @param currencyAmount 数量
     * @param reason 赠送原因
     */
    public static void onReward(float currencyAmount, String reason) {
        try {
            TDGAVirtualCurrency.onReward(currencyAmount, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录付费点
     * @param item
     * @param itemNumber
     * @param priceInVirtualCurrency
     */
    public static void onPurchase(String item, int itemNumber, float priceInVirtualCurrency) {
        log("onPurchase item=" + item + " itemNumber=" + itemNumber + " priceInVirtualCurrency=" + priceInVirtualCurrency);
        try {
            TDGAItem.onPurchase(item, itemNumber, priceInVirtualCurrency);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * //消耗物品或服务等
     * @param item
     * @param itemNumber
     */
    public static void onUse(String item, int itemNumber) {
        try {
            TDGAItem.onUse(item, itemNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义事件统计
     Map<String, Object> map = new HashMap<String, Object>();
     map.put("level", "50-60");     //级别区间，注意是字符串哟！
     map.put("map", "沼泽地阿卡村"); //地图场景
     map.put("mission", "屠龙副本"); //关卡。
     map.put("reason", "PK致死"); //死亡原因
     map.put("coin", "10000～20000"); //携带金币数量
     TalkingDataGA.onEvent("play", map);
     */
    public static void appOnEvent(String eventId, String json) {
        try {
            Map<String, String> map = new HashMap<>();
            JSONObject obj = new JSONObject(json);
            Iterator<?> it = obj.keys();
            while (it.hasNext()) {//遍历JSONObject
                String key = (String) it.next().toString();
                String value = obj.getString(key);
                map.put(key, value);
            }
            TalkingDataGA.onEvent(eventId, map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
